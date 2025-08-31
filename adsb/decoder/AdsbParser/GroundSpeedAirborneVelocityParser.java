package decoder.AdsbParser;

import modes.raw.AirborneVelocity;
import modes.raw.GroundSpeedAirborneVelocity;

public class GroundSpeedAirborneVelocityParser extends AirborneVelocityParser {
    private GroundSpeedAirborneVelocity adsbMessage;

    private int velocityEastWest;
    private int velocitySouthWest;


    public GroundSpeedAirborneVelocityParser(long timeStamp, long highBits, long lowBits){
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 40) & bitMasks[24]);

        int subType = (int)((ME >> 16) & 0x7);
        setVelocityEastWest((int)((ME >> (56 - 14)) & 0x01), (int)((ME >> (56 - 24)) & 0x3FF), subType);
        setVelocitySouthWest((int)((ME >> (56 - 25)) & 0x01), (int)((ME >> (56 - 35)) & 0x3FF), subType);
    }
    public GroundSpeedAirborneVelocity getModeSMessage(){
        this.adsbMessage = new GroundSpeedAirborneVelocity();
        adsbMessage.setTimeStamp(getTimeStamp());
        adsbMessage.setDownlinkFormat(getDownlinkFormat());
        adsbMessage.setTypeCode(getTypeCode());
        adsbMessage.setTransponderCapability(getTransponderCapability());
        adsbMessage.setIcaoAddress(getIcaoAddress());
        adsbMessage.setSubType(getSubType());
        adsbMessage.setIntentChangeFlag(getIntentChangeFlag());
        adsbMessage.setIfrCapabilityFLag(getIfrCapabilityFLag());
        adsbMessage.setNACv(getNACv());
        adsbMessage.setTypeVerticalRate(getTypeVerticalRate());
        adsbMessage.setVerticalRate(getVerticalRate());
        adsbMessage.setAltitudeDifference(getAltitudeDifference());
        adsbMessage.setVelocityEastWest(getVelocityEastWest());
        adsbMessage.setVelocitySouthWest(getVelocitySouthWest());
        return adsbMessage;
    }

    public void setVelocityEastWest(int sign, int value, int subType) {
        if (value != 0){
            this.velocityEastWest = value - 1;
            if(sign == 1){
                this.velocityEastWest = -this.velocityEastWest;
            }
            if(subType == 2){
                this.velocityEastWest *= 4;
            }
        }
    }

    public void setVelocitySouthWest(int sign, int value, int subType) {
        if (value != 0){
            this.velocitySouthWest = value - 1;
            if(sign == 1){
                this.velocitySouthWest = -this.velocitySouthWest;
            }
            if(subType == 2){
                this.velocitySouthWest *= 4;
            }
        }
    }

    public int getVelocityEastWest() {
        return velocityEastWest;
    }

    public void setVelocityEastWest(int velocityEastWest) {
        this.velocityEastWest = velocityEastWest;
    }

    public int getVelocitySouthWest() {
        return velocitySouthWest;
    }

    public void setVelocitySouthWest(int velocitySouthWest) {
        this.velocitySouthWest = velocitySouthWest;
    }
}
