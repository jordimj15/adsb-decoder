package decoder;
import decoder.AdsbParser.*;
import modes.AdsbMessage;
import modes.raw.AirborneVelocity;
import modes.raw.AircraftIdentification;
import modes.raw.SurfacePosition;

/**
 * Entry point of the decoder: turns one line of raw receiver output into a
 * typed ADS-B message.
 *
 * <p>Each input line is expected to follow the format
 * {@code @<timestamp>*<hexMessage>}, terminated by a semicolon, where
 * {@code timestamp} is the reception time as a Unix epoch in microseconds
 * and {@code hexMessage} is the Mode S frame in hexadecimal.
 *
 * <p>Only 112-bit frames (28 hexadecimal characters) with Downlink Format 17
 * (ADS-B Extended Squitter) are decoded. The Type Code of the 56-bit ME
 * field, and its subtype where needed, selects the parser from
 * {@code decoder.AdsbParser} that decodes the message:
 * <ul>
 *   <li>TC 1-4: Aircraft Identification</li>
 *   <li>TC 5-8: Surface Position</li>
 *   <li>TC 9-18 and 20-22: Airborne Position</li>
 *   <li>TC 19, subtypes 1-2: Airborne Velocity (ground speed)</li>
 *   <li>TC 19, subtypes 3-4: Airborne Velocity (air speed)</li>
 *   <li>TC 31, subtype 0: Airborne Operational Status</li>
 *   <li>TC 31, subtype 1: Surface Operational Status</li>
 * </ul>
 * Any other frame length, Downlink Format, Type Code or subtype is ignored.
 */
public class Decoder {
    /** Lookup table where entry {@code i} is a mask with the {@code i} lowest bits set. */
    private static final long[] bitMasks = new long[64];
    static {
        for (int i = 0; i < 64; i++) {
            bitMasks[i] = (1L << i) - 1;
        }
    }

    public Decoder() {
    }

    /**
     * Decodes a single line of raw receiver output.
     *
     * @param rawData the raw line, in the {@code @<timestamp>*<hexMessage>} format terminated by a semicolon
     * @return the decoded message, as the {@link AdsbMessage} subclass matching its type, or
     * {@code null} if the frame is not a supported DF17 message
     */
    public AdsbMessage decode(String rawData) {
        int DF;
        int CA;
        String icao24;
        long highBits;
        long lowBits;
        long ME;
        long timestamp;
        int TC;

        timestamp = Long.parseLong(rawData.substring(rawData.indexOf("@") + 1, rawData.indexOf("*")));
        String hexadecimalMessage = rawData.substring(rawData.indexOf("*") + 1, rawData.indexOf(";"));

        AdsbMessage adsbMessage = null;

        if(hexadecimalMessage.length() == 28) {
            // A 112-bit frame does not fit in one long, so the first 16
            // hexadecimal characters (64 bits) go to highBits and the
            // remaining 12 (48 bits) go to lowBits.
            highBits = Long.parseUnsignedLong(hexadecimalMessage.substring(0, 16), 16);
            lowBits  = Long.parseUnsignedLong(hexadecimalMessage.substring(16), 16);
            // Rebuild the 56-bit ME field from the last 32 bits of highBits
            // and the first 24 bits of lowBits.
            ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);

            DF = (int)((highBits >>> 59) & 0x1F);
            CA = (int)((highBits >>> 56) & 0x07);

            switch(DF) {
                case 17:
                    // The subtype (ME bits 6-8) tells apart the variants of
                    // the Airborne Velocity and Operational Status messages.
                    int subType = (int)((ME >>> (55 - 7)) & 0x7);
                    TC = (int)(ME >>> 51);
                    switch (TC){
                        case 1: case 2: case 3: case 4:
                            adsbMessage = new AircraftIdentificationParser(timestamp, highBits, lowBits).getModeSMessage();
                            break;
                        case  5: case 6: case 7: case 8:
                            adsbMessage = new SurfacePositionParser(timestamp, highBits, lowBits).getModeSMessage();
                            break;
                        case 9: case 10: case 11: case 12: case 13: case 14: case 15: case  16: case 17: case 18: case 20: case 21: case 22:
                            adsbMessage = new AirbornePositionParser(timestamp, highBits, lowBits).getModeSMessage();
                            break;
                        case 19:
                            if (subType == 1 || subType == 2) {
                                adsbMessage = new GroundSpeedAirborneVelocityParser(timestamp, highBits, lowBits).getModeSMessage();
                            } else if (subType == 3 || subType == 4) {
                                adsbMessage = new AirSpeedAirborneVelocityParser(timestamp, highBits, lowBits).getModeSMessage();
                            }
                            break;
                        case 31:
                            if (subType == 0) {
                                adsbMessage = new AirborneOperationalStatusParser(timestamp, highBits, lowBits).getModeSMessage();
                            } else if (subType == 1) {
                                adsbMessage = new SurfaceOperationalStatusParser(timestamp, highBits, lowBits).getModeSMessage();
                            }
                    }
                    break;
            }
        }

        return(adsbMessage != null) ? adsbMessage : null;
    }
}
