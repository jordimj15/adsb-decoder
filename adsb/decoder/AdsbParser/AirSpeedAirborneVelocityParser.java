package decoder.AdsbParser;

import modes.raw.AirSpeedAirborneVelocity;

public class AirSpeedAirborneVelocityParser extends AirborneVelocityParser {
    private AirSpeedAirborneVelocity adsbMessage;

    private Double heading;
    private int headingStatus;
    private String airspeedType;
    private double airspeed;

    public AirSpeedAirborneVelocityParser(long timeStamp, long highBits, long lowBits) {
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 40) & bitMasks[24]);
    }
}
