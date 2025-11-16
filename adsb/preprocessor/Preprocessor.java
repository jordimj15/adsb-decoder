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

public class Preprocessor {
    private static final long CLEAN_QUEUES_RATE = 10000000L;
    private static final long MAX_AIR_CPR_DIFF_TIME = 10000000L;
    private static final long MAX_SURF_CPR_DIFF_TIME = 50000000L;
    private static final long MAX_OUT_QUEUE_TIME = 600000000L;
    private static final long MAX_CALLSIGN_TIME = 600000000L;
    private long cleanedTimeStamp = 0;

    private Map<String, IdMessage> callSignMap = new HashMap<>();
    private List<AdsbMessage> outgoingMessageList = new ArrayList<>();
    private Map<String, Deque<AirbornePosition>> airborneMapEven = new HashMap<>();
    private Map<String, Deque<AirbornePosition>> airborneMapOdd  = new HashMap<>();
    private Map<String, Deque<SurfacePosition>> surfaceMapEven = new HashMap<>();
    private Map<String, Deque<SurfacePosition>> surfaceMapOdd  = new HashMap<>();

    public List<AdsbMessage> preprocess(AdsbMessage adsbMessage) throws Exception {
        long currentTimeStamp = adsbMessage.getTimeStamp();
        List<AdsbMessage> processed = new ArrayList<>();

        if ((currentTimeStamp - cleanedTimeStamp) < 0) {
            callSignMap.clear();
            outgoingMessageList.clear();
            airborneMapEven.clear();
            airborneMapOdd.clear();
            surfaceMapEven.clear();
            surfaceMapOdd.clear();
            cleanedTimeStamp = currentTimeStamp;
        }

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
                    break; // mensajes demasiado viejos, no tiene sentido seguir
                }
            }
        }
    }

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

    private void checkCallsign(AdsbMessage message) {
        IdMessage id = callSignMap.get(message.getIcaoAddress());
        if (id == null) return;

        if (message.getTimeStamp() > id.getLastIdMessageTS()) {
            message.setCallsign(id.getCallsign());
            id.setLastPositionMessageTS(message.getTimeStamp());
        }
    }

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

        DLatE = (360.0 * correctionFactor) / 60.0;
        DLatO = (360.0 * correctionFactor) / 59.0;

        double latCprE = evenMessage.getEncodedLatitude();
        double latCprO = oddMessage.getEncodedLatitude();
        double lonCprE = evenMessage.getEncodedLongitude();
        double lonCprO = oddMessage.getEncodedLongitude();

        if(latCprE == 0.0 || lonCprE == 0.0 || latCprO == 0.0 || lonCprO == 0.0) return null;

        double j = Math.floor(59.0 * latCprE - 60.0 * latCprO + 0.5);

        double latE = DLatE * (module(j, 60) + latCprE);
        if(latE >= 270) latE -= 360; else if(latE >= 90) latE = 180 - latE;
        else if(latE <= -270) latE += 360; else if(latE <= -90) latE = -(180 + latE);

        double latO = DLatO * (module(j, 59) + latCprO);
        if(latO >= 270) latO -= 360; else if(latO >= 90) latO = 180 - latO;
        else if(latO <= -270) latO += 360; else if(latO <= -90) latO = -(180 + latO);

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

    private double module(double a, double b){
        return ((a % b) + b) % b;
    }

    private Position findNewerMessage(Position first, Position second) {
        return (first.getTimeStamp() > second.getTimeStamp()) ? first : second;
    }

    private double cprNL(double lat) throws Exception {
        // La función completa que ya tenías
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

    private double cprN(double lat, int isOdd) throws Exception {
        double N = cprNL(lat) - isOdd;
        return (N > 1) ? N : 1;
    }

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
