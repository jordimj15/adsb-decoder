package decoder;
import decoder.AdsbParser.*;
import modes.AdsbMessage;
import modes.raw.AirborneVelocity;
import modes.raw.AircraftIdentification;
import modes.raw.SurfacePosition;

public class Decoder {
    private static final long[] bitMasks = new long[64];
    static {
        for (int i = 0; i < 64; i++) {
            bitMasks[i] = (1L << i) - 1;
        }
    }

    public Decoder() {
    }

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
            highBits = Long.parseUnsignedLong(hexadecimalMessage.substring(0, 16), 16);
            lowBits  = Long.parseUnsignedLong(hexadecimalMessage.substring(16), 16);
            ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);

            DF = (int)((highBits >>> 59) & 0x1F);
            CA = (int)((highBits >>> 56) & 0x07);

            switch(DF) {
                case 17:
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
