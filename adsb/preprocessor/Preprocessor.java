package preprocessor;

import modes.AdsbMessage;
import modes.computed.ComputedAirbornePosition;
import modes.computed.ComputedPosition;
import modes.computed.ComputedSurfacePosition;
import modes.raw.AirbornePosition;
import modes.raw.AircraftIdentification;
import modes.raw.Position;
import modes.raw.SurfacePosition;
import java.util.*;

/**
 * Second stage of the decoding pipeline: completes the messages coming out
 * of the {@code Decoder} by resolving positions and attaching
 * identification data.
 *
 * <p>Messages must be fed one at a time, in reception order, through
 * {@link #preprocess(AdsbMessage)}. The preprocessor keeps state for every
 * aircraft seen recently, identified by its ICAO address, and does three
 * things:
 * <ol>
 *   <li><b>Position resolution:</b> raw position messages only carry CPR
 *       encoded coordinates. Each one is stored in an even or odd queue for
 *       its aircraft, and when an even/odd pair is found close enough in
 *       time ({@link #MAX_AIR_CPR_DIFF_TIME} for airborne positions,
 *       {@link #MAX_SURF_CPR_DIFF_TIME} for surface positions), global CPR
 *       decoding produces a {@link ComputedAirbornePosition} or a
 *       {@link ComputedSurfacePosition}. Raw positions are never returned
 *       themselves.</li>
 *   <li><b>Identification:</b> callsign and aircraft category are only
 *       transmitted in Aircraft Identification messages. They are kept per
 *       aircraft in an {@link IdMessage} and attached to every other message
 *       from the same aircraft. Messages from an aircraft with no known
 *       identification yet are held back, and released once one arrives.
 *       Aircraft Identification messages themselves are consumed and not
 *       returned.</li>
 *   <li><b>Cleanup:</b> every {@link #CLEAN_QUEUES_RATE}, stale CPR
 *       messages, identifications and held back messages are discarded. If a
 *       message arrives with an earlier timestamp than the last cleanup, for
 *       example because a new input file has started, all state is cleared.</li>
 * </ol>
 *
 * <p>All timestamps and time constants are in microseconds.
 */
public class Preprocessor {
    /** Minimum time between two cleanups of the stored state (10 s). */
    private static final long CLEAN_QUEUES_RATE = 10000000L;
    /** Maximum time between an even and an odd airborne position message for them to be paired (10 s). */
    private static final long MAX_AIR_CPR_DIFF_TIME = 10000000L;
    /** Maximum time between an even and an odd surface position message for them to be paired (50 s). */
    private static final long MAX_SURF_CPR_DIFF_TIME = 50000000L;
    /** Maximum time a message is held back waiting for an identification before being discarded (10 min). */
    private static final long MAX_OUT_QUEUE_TIME = 600000000L;
    /** Maximum time an identification is kept without a new Aircraft Identification message (10 min). */
    private static final long MAX_CALLSIGN_TIME = 600000000L;
    /** Timestamp of the last cleanup of the stored state. */
    private long cleanedTimeStamp = 0;

    /** Identification of each aircraft, by ICAO address. */
    private Map<String, IdMessage> callSignMap = new HashMap<>();
    /** Messages held back until an identification is received for their aircraft. */
    private List<AdsbMessage> outgoingMessageList = new ArrayList<>();
    /** Recent even airborne position messages of each aircraft, by ICAO address, newest first. */
    private Map<String, Deque<AirbornePosition>> airborneMapEven = new HashMap<>();
    /** Recent odd airborne position messages of each aircraft, by ICAO address, newest first. */
    private Map<String, Deque<AirbornePosition>> airborneMapOdd  = new HashMap<>();
    /** Recent even surface position messages of each aircraft, by ICAO address, newest first. */
    private Map<String, Deque<SurfacePosition>> surfaceMapEven = new HashMap<>();
    /** Recent odd surface position messages of each aircraft, by ICAO address, newest first. */
    private Map<String, Deque<SurfacePosition>> surfaceMapOdd  = new HashMap<>();

    /**
     * Processes one decoded message and returns the messages that are ready
     * to be used.
     *
     * <p>The returned list is empty when nothing is ready yet (a raw position
     * with no pair, a message waiting for an identification, or an Aircraft
     * Identification message). It can also hold several messages, when an
     * identification releases the messages held back for that aircraft.
     *
     * @param adsbMessage the message decoded by the {@code Decoder}
     * @return the messages ready to be used, with their callsign and aircraft category attached
     * @throws Exception if CPR decoding is run on two messages that are not an even/odd pair
     */
    public List<AdsbMessage> preprocess(AdsbMessage adsbMessage) throws Exception {
        long currentTimeStamp = adsbMessage.getTimeStamp();
        List<AdsbMessage> processed = new ArrayList<>();

        // A timestamp earlier than the last cleanup means a new input has
        // started, so the state kept from the previous one is discarded.
        if ((currentTimeStamp - cleanedTimeStamp) < 0) {
            callSignMap.clear();
            outgoingMessageList.clear();
            airborneMapEven.clear();
            airborneMapOdd.clear();
            surfaceMapEven.clear();
            surfaceMapOdd.clear();
            cleanedTimeStamp = currentTimeStamp;
        }

        // Periodically discard stale state so that the queues and maps do
        // not grow without bound.
        if ((currentTimeStamp - cleanedTimeStamp) > CLEAN_QUEUES_RATE) {
            cleanQueuesOptimized(currentTimeStamp);
            cleanedTimeStamp = currentTimeStamp;
        }

        if (adsbMessage instanceof AircraftIdentification) {
            processIdentification((AircraftIdentification) adsbMessage, processed);
        } else if (adsbMessage instanceof AirbornePosition) {
            processAirbornePosition((AirbornePosition) adsbMessage, processed);
        } else if (adsbMessage instanceof SurfacePosition) {
            processSurfacePosition((SurfacePosition) adsbMessage, processed);
        } else {
            processOtherMessage(adsbMessage, processed);
        }

        return processed;
    }

    /**
     * Stores or updates the identification of an aircraft, and releases the
     * messages from it that were waiting for one.
     *
     * <p>For an aircraft that is already known, only the callsign and the
     * last identification timestamp are updated. The aircraft category stays
     * the one from its first identification.
     *
     * @param adsbMessage the Aircraft Identification message
     * @param processed the list of messages ready to be returned, where the released messages are added
     */
    private void processIdentification(AircraftIdentification adsbMessage, List<AdsbMessage> processed) {
        IdMessage id = callSignMap.get(adsbMessage.getIcaoAddress());

        if (id == null) {
            id = new IdMessage(adsbMessage);
            callSignMap.put(adsbMessage.getIcaoAddress(), id);
        } else {
            id.setCallsign(adsbMessage.getCallsign());
            id.setLastIdMessageTS(adsbMessage.getTimeStamp());
        }

        processPendingMessages(adsbMessage.getIcaoAddress(), id, processed);

    }

    /**
     * Attaches the identification to every held back message from the given
     * aircraft, and moves those messages from the held back list to the
     * processed list.
     *
     * @param icao24 the ICAO address of the aircraft
     * @param id the identification of the aircraft
     * @param processed the list of messages ready to be returned
     */
    private void processPendingMessages(String icao24, IdMessage id, List<AdsbMessage> processed) {
        outgoingMessageList.removeIf(adsbMessage -> {
            if (adsbMessage.getIcaoAddress().equals(icao24)) {
                adsbMessage.setCallsign(id.getCallsign());
                adsbMessage.setAircraftCategory(id.getAircraftCategory());
                processed.add(0, adsbMessage);
                if (adsbMessage instanceof AirbornePosition || adsbMessage instanceof SurfacePosition) {
                    callSignMap.get(icao24).setLastIdMessageTS(adsbMessage.getTimeStamp());
                }
                return true;
            }
            return false;
        });

    }

    /**
     * Stores a raw airborne position in its even or odd queue, and tries to
     * pair it with the most recent message of the opposite CPR format from
     * the same aircraft.
     *
     * <p>If that message is within {@link #MAX_AIR_CPR_DIFF_TIME}, the pair is
     * decoded with {@link #runCprDecoder} and the resulting position is
     * handled by {@link #processOtherMessage}, like any other message.
     * Otherwise nothing is produced, and the message stays in its queue
     * waiting for a message of the opposite format.
     *
     * @param adsbMessage the raw airborne position message
     * @param processed the list of messages ready to be returned
     * @throws Exception if CPR decoding is run on two messages that are not an even/odd pair
     */
    private void processAirbornePosition(AirbornePosition adsbMessage, List<AdsbMessage> processed) throws Exception {
        boolean isEven = adsbMessage.getCprFormat().equals("Even");
        String icao24 = adsbMessage.getIcaoAddress();

        Deque<AirbornePosition> sourceQueue = (isEven
                ? airborneMapEven.computeIfAbsent(icao24, k -> new ArrayDeque<>())
                : airborneMapOdd.computeIfAbsent(icao24, k -> new ArrayDeque<>()));

        Deque<AirbornePosition> targetQueue = (isEven
                ? airborneMapOdd.get(icao24)
                : airborneMapEven.get(icao24));

        sourceQueue.addFirst(adsbMessage);

        // Queues hold the newest message first, so only the most recent
        // message of the opposite format is checked. If it is too old, every
        // older one is too.
        if (targetQueue != null && !targetQueue.isEmpty()) {
            for (AirbornePosition opposite : targetQueue) {
                long diff = Math.abs(adsbMessage.getTimeStamp() - opposite.getTimeStamp());
                if (diff <= MAX_AIR_CPR_DIFF_TIME) {
                    ComputedAirbornePosition computed = (ComputedAirbornePosition) runCprDecoder(adsbMessage, opposite);
                    if (computed != null) {
                        processOtherMessage(computed, processed);
                    }
                    break;
                } else if (diff > MAX_AIR_CPR_DIFF_TIME) {
                    break;
                }
            }
        }
    }

    /**
     * Stores a raw surface position in its even or odd queue, and tries to
     * pair it with the most recent message of the opposite CPR format from
     * the same aircraft.
     *
     * <p>Works like {@link #processAirbornePosition}, using
     * {@link #MAX_SURF_CPR_DIFF_TIME} as the pairing window. Two messages
     * with the same timestamp are not paired.
     *
     * @param adsbMessage the raw surface position message
     * @param processed the list of messages ready to be returned
     * @throws Exception if CPR decoding is run on two messages that are not an even/odd pair
     */
    private void processSurfacePosition(SurfacePosition adsbMessage, List<AdsbMessage> processed) throws Exception {
        boolean isEven = adsbMessage.getCprFormat().equals("Even");
        String icao24 = adsbMessage.getIcaoAddress();

        Deque<SurfacePosition> sourceQueue = (isEven
                ? surfaceMapEven.computeIfAbsent(icao24, k -> new ArrayDeque<>())
                : surfaceMapOdd.computeIfAbsent(icao24, k -> new ArrayDeque<>()));

        Deque<SurfacePosition> targetQueue = (isEven
                ? surfaceMapOdd.get(icao24)
                : surfaceMapEven.get(icao24));

        sourceQueue.addFirst(adsbMessage);

        // Same as for airborne positions, only the most recent message of
        // the opposite format is checked.
        if (targetQueue != null && !targetQueue.isEmpty()) {
            for (SurfacePosition opposite : targetQueue) {
                long diff = Math.abs(adsbMessage.getTimeStamp() - opposite.getTimeStamp());
                if (diff > 0 && diff <= MAX_SURF_CPR_DIFF_TIME) {
                    ComputedSurfacePosition computed =
                            (ComputedSurfacePosition) runCprDecoder(adsbMessage, opposite);
                    if (computed != null) {
                        processOtherMessage(computed, processed);
                    }
                    break;
                } else if (diff > MAX_SURF_CPR_DIFF_TIME) {
                    break;
                }
            }
        }
    }

    /**
     * Attaches the identification of its aircraft to a message and adds it
     * to the processed list, or holds it back if no identification is known
     * yet for that aircraft.
     *
     * @param message the message to complete
     * @param preprocessed the list of messages ready to be returned
     */
    private void processOtherMessage(AdsbMessage message, List<AdsbMessage> preprocessed) {
        String icao24 = message.getIcaoAddress();
        IdMessage id = callSignMap.get(icao24);

        if (id != null) {
            message.setCallsign(id.getCallsign());
            message.setAircraftCategory(id.getAircraftCategory());
            preprocessed.add(0, message);
        } else {
            outgoingMessageList.add(0, message);
        }
    }

    /**
     * Attaches the known callsign to a message if it is newer than the last
     * identification of its aircraft, and records its timestamp as the last
     * position message of that aircraft. Currently not used.
     *
     * @param message the message to complete
     */
    private void checkCallsign(AdsbMessage message) {
        IdMessage id = callSignMap.get(message.getIcaoAddress());
        if (id == null) return;

        if (message.getTimeStamp() > id.getLastIdMessageTS()) {
            message.setCallsign(id.getCallsign());
            id.setLastPositionMessageTS(message.getTimeStamp());
        }
    }

    /**
     * Resolves the position of an aircraft from an even and an odd CPR
     * encoded position message, using global CPR decoding as described in
     * DO-260B.
     *
     * <p>The two messages can be given in any order. The resulting position
     * takes all its other fields from the newer message, and its latitude is
     * the one decoded from the newer message too. Surface positions use a
     * quarter of the airborne zone sizes (90° instead of 360°).
     *
     * <p>No position is returned if any of the encoded coordinates is zero,
     * or if the even and odd latitudes fall in different longitude zones
     * (see {@link #cprNL}), since the pair cannot be decoded reliably then.
     *
     * <p>The decoded longitude is normalised to the range from -180° to 180°,
     * and then moved into the range from -30° to 60° in steps of 90°. This is
     * done for both airborne and surface positions. It picks the right
     * solution among the four possible ones of a surface position for a
     * receiver located within that range, but it also moves any airborne
     * position outside it.
     *
     * @param firstMessage one of the two position messages
     * @param secondMessage the other position message, of the opposite CPR format
     * @return the resolved position, a {@link ComputedAirbornePosition} or a
     * {@link ComputedSurfacePosition}, or {@code null} if the pair cannot be decoded
     * @throws Exception if the two messages are not an even/odd pair
     */
    private ComputedPosition runCprDecoder(Position firstMessage, Position secondMessage) throws Exception {
        Position evenMessage;
        Position oddMessage;
        ComputedPosition newPosition;
        double latitude;
        double longitude;
        double ni;
        double m;
        double DLatE;
        double DLatO;
        double correctionFactor;

        Position newerMessage = findNewerMessage(firstMessage, secondMessage);

        if(newerMessage instanceof AirbornePosition) {
            newPosition = new ComputedAirbornePosition((AirbornePosition) newerMessage);
            correctionFactor = 1;
        } else {
            newPosition = new ComputedSurfacePosition((SurfacePosition) newerMessage);
            correctionFactor = 0.25;
        }

        if(firstMessage.getCprFormat().equals("Even") && secondMessage.getCprFormat().equals("Odd")){
            evenMessage = firstMessage;
            oddMessage = secondMessage;
        } else if(secondMessage.getCprFormat().equals("Even") && firstMessage.getCprFormat().equals("Odd")){
            evenMessage = secondMessage;
            oddMessage = firstMessage;
        } else{
            throw new Exception("Invalid CPR format");
        }

        // Latitude zone sizes for the even (60 zones) and odd (59 zones) formats.
        DLatE = (360.0 * correctionFactor) / 60.0;
        DLatO = (360.0 * correctionFactor) / 59.0;

        double latCprE = evenMessage.getEncodedLatitude();
        double latCprO = oddMessage.getEncodedLatitude();
        double lonCprE = evenMessage.getEncodedLongitude();
        double lonCprO = oddMessage.getEncodedLongitude();

        if(latCprE == 0.0 || lonCprE == 0.0 || latCprO == 0.0 || lonCprO == 0.0) return null;

        // Latitude zone index, then the even and odd latitudes folded into
        // the range from -90° to 90°.
        double j = Math.floor(59.0 * latCprE - 60.0 * latCprO + 0.5);

        double latE = DLatE * (module(j, 60) + latCprE);
        if(latE >= 270) latE -= 360; else if(latE >= 90) latE = 180 - latE;
        else if(latE <= -270) latE += 360; else if(latE <= -90) latE = -(180 + latE);

        double latO = DLatO * (module(j, 59) + latCprO);
        if(latO >= 270) latO -= 360; else if(latO >= 90) latO = 180 - latO;
        else if(latO <= -270) latO += 360; else if(latO <= -90) latO = -(180 + latO);

        // Both latitudes must fall in the same longitude zone, otherwise the
        // aircraft crossed a zone boundary between the two messages. The
        // longitude is then decoded with the zone count of the newer message.
        if(cprNL(latE) == cprNL(latO)){
            if(newerMessage.getCprFormat().equals("Even")){
                latitude = latE;
                ni = cprN(latE, 0);
                m = Math.floor(lonCprE * (cprNL(latE) - 1) - lonCprO * cprNL(latE) + 0.5);
                longitude = (360.0 * correctionFactor) / ni * (module(m, ni) + lonCprE);
            } else {
                latitude = latO;
                ni = cprN(latO, 1);
                m = Math.floor(lonCprE * (cprNL(latO) - 1) - lonCprO * cprNL(latO) + 0.5);
                longitude = (360.0 * correctionFactor) / ni * (module(m, ni) + lonCprO);
            }

            // Normalise to the range from -180° to 180°, then move into the
            // range from -30° to 60° (see the method description).
            if(longitude > 180) longitude -= 360;
            while (longitude > 60) longitude -= 90;
            while (longitude < -30) longitude += 90;
        } else{
            return null;
        }

        newPosition.setComputedLatitude(latitude);
        newPosition.setComputedLongitude(longitude);

        return newPosition;
    }

    /**
     * Modulo operation that always returns a value in the range from 0 to
     * {@code b}, unlike the {@code %} operator, which keeps the sign of {@code a}.
     *
     * @param a the dividend
     * @param b the divisor
     * @return the non negative remainder of {@code a} divided by {@code b}
     */
    private double module(double a, double b){
        return ((a % b) + b) % b;
    }

    /**
     * Returns the newer of two position messages. If both have the same
     * timestamp, the second one is returned.
     *
     * @param first one of the position messages
     * @param second the other position message
     * @return the message with the latest timestamp
     */
    private Position findNewerMessage(Position first, Position second) {
        return (first.getTimeStamp() > second.getTimeStamp()) ? first : second;
    }

    /**
     * Computes the number of longitude zones (NL) at a given latitude, using
     * the precomputed transition latitudes of the DO-260B NL table.
     *
     * @param lat the latitude, in decimal degrees
     * @return the number of longitude zones, from 1 to 59
     */
    private double cprNL(double lat) throws Exception {
        double NL;
        lat = Math.abs(lat);
        if (lat < 10.4704713) NL = 59;
        else if (lat < 14.82817437) NL = 58;
        else if (lat < 18.18626357) NL = 57;
        else if (lat < 21.02939493) NL = 56;
        else if (lat < 23.54504487) NL = 55;
        else if (lat < 25.82924707) NL = 54;
        else if (lat < 27.93898710) NL = 53;
        else if (lat < 29.91135686) NL = 52;
        else if (lat < 31.77209708) NL = 51;
        else if (lat < 33.53993436) NL = 50;
        else if (lat < 35.22899598) NL = 49;
        else if (lat < 36.85025108) NL = 48;
        else if (lat < 38.41241892) NL = 47;
        else if (lat < 39.92256684) NL = 46;
        else if (lat < 41.38651832) NL = 45;
        else if (lat < 42.80914012) NL = 44;
        else if (lat < 44.19454951) NL = 43;
        else if (lat < 45.54626723) NL = 42;
        else if (lat < 46.86733252) NL = 41;
        else if (lat < 48.16039128) NL = 40;
        else if (lat < 49.42776439) NL = 39;
        else if (lat < 50.67150166) NL = 38;
        else if (lat < 51.89342469) NL = 37;
        else if (lat < 53.09516153) NL = 36;
        else if (lat < 54.27817472) NL = 35;
        else if (lat < 55.44378444) NL = 34;
        else if (lat < 56.59318756) NL = 33;
        else if (lat < 57.72747354) NL = 32;
        else if (lat < 58.84763776) NL = 31;
        else if (lat < 59.95459277) NL = 30;
        else if (lat < 61.04917774) NL = 29;
        else if (lat < 62.13216659) NL = 28;
        else if (lat < 63.20427479) NL = 27;
        else if (lat < 64.26616523) NL = 26;
        else if (lat < 65.31845310) NL = 25;
        else if (lat < 66.36171008) NL = 24;
        else if (lat < 67.39646774) NL = 23;
        else if (lat < 68.42322022) NL = 22;
        else if (lat < 69.44242631) NL = 21;
        else if (lat < 70.45451075) NL = 20;
        else if (lat < 71.45986473) NL = 19;
        else if (lat < 72.45884545) NL = 18;
        else if (lat < 73.45177442) NL = 17;
        else if (lat < 74.43893416) NL = 16;
        else if (lat < 75.42056257) NL = 15;
        else if (lat < 76.39684391) NL = 14;
        else if (lat < 77.36789461) NL = 13;
        else if (lat < 78.33374083) NL = 12;
        else if (lat < 79.29428225) NL = 11;
        else if (lat < 80.24923213) NL = 10;
        else if (lat < 81.19801349) NL = 9;
        else if (lat < 82.13956981) NL = 8;
        else if (lat < 83.07199445) NL = 7;
        else if (lat < 83.99173563) NL = 6;
        else if (lat < 84.89166191) NL = 5;
        else if (lat < 85.75541621) NL = 4;
        else if (lat < 86.53536998) NL = 3;
        else if (lat < 87.00000000) NL = 2;
        else NL = 1;
        return NL;
    }

    /**
     * Computes the number of longitude zones for a latitude and CPR format,
     * which is NL for even messages and NL - 1 for odd messages, with a
     * minimum of 1.
     *
     * @param lat the latitude, in decimal degrees
     * @param isOdd {@code 0} for an even message, {@code 1} for an odd message
     * @return the number of longitude zones for that format
     */
    private double cprN(double lat, int isOdd) throws Exception {
        double N = cprNL(lat) - isOdd;
        return (N > 1) ? N : 1;
    }

    /**
     * Discards the stale state of every aircraft: CPR messages older than
     * their pairing window, identifications not refreshed within
     * {@link #MAX_CALLSIGN_TIME}, and held back messages older than
     * {@link #MAX_OUT_QUEUE_TIME}. CPR queues left empty are removed from
     * their map.
     *
     * @param currentTimeStamp the timestamp of the message being processed, used as the current time
     */
    private void cleanQueuesOptimized(long currentTimeStamp) {

        Iterator<Map.Entry<String, Deque<AirbornePosition>>> itAE = airborneMapEven.entrySet().iterator();
        while (itAE.hasNext()) {
            Map.Entry<String, Deque<AirbornePosition>> e = itAE.next();
            Deque<AirbornePosition> q = e.getValue();
            q.removeIf(msg -> currentTimeStamp - msg.getTimeStamp() > MAX_AIR_CPR_DIFF_TIME);
            if (q.isEmpty()) {
                itAE.remove();
            }
        }

        Iterator<Map.Entry<String, Deque<AirbornePosition>>> itAO = airborneMapOdd.entrySet().iterator();
        while (itAO.hasNext()) {
            Map.Entry<String, Deque<AirbornePosition>> e = itAO.next();
            Deque<AirbornePosition> q = e.getValue();
            q.removeIf(msg -> currentTimeStamp - msg.getTimeStamp() > MAX_AIR_CPR_DIFF_TIME);
            if (q.isEmpty()) {
                itAO.remove();
            }
        }

        Iterator<Map.Entry<String, Deque<SurfacePosition>>> itSE = surfaceMapEven.entrySet().iterator();
        while (itSE.hasNext()) {
            Map.Entry<String, Deque<SurfacePosition>> e = itSE.next();
            Deque<SurfacePosition> q = e.getValue();
            q.removeIf(msg -> currentTimeStamp - msg.getTimeStamp() > MAX_SURF_CPR_DIFF_TIME);
            if (q.isEmpty()) {
                itSE.remove();
            }
        }

        Iterator<Map.Entry<String, Deque<SurfacePosition>>> itSO = surfaceMapOdd.entrySet().iterator();
        while (itSO.hasNext()) {
            Map.Entry<String, Deque<SurfacePosition>> e = itSO.next();
            Deque<SurfacePosition> q = e.getValue();
            q.removeIf(msg -> currentTimeStamp - msg.getTimeStamp() > MAX_SURF_CPR_DIFF_TIME);
            if (q.isEmpty()) {
                itSO.remove();
            }
        }

        Iterator<Map.Entry<String, IdMessage>> itCS = callSignMap.entrySet().iterator();
        while (itCS.hasNext()) {
            Map.Entry<String, IdMessage> e = itCS.next();
            IdMessage id = e.getValue();

            long diff = currentTimeStamp - id.getLastIdMessageTS();
            if (diff > MAX_CALLSIGN_TIME) {
                itCS.remove();
            }
        }
        outgoingMessageList.removeIf(msg ->
                currentTimeStamp - msg.getTimeStamp() > MAX_OUT_QUEUE_TIME
        );
    }

}
