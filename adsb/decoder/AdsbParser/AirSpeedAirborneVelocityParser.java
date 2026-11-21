package decoder.AdsbParser;

import modes.raw.AirSpeedAirborneVelocity;
import modes.raw.AirborneVelocity;

public class AirSpeedAirborneVelocityParser extends AirborneVelocityParser {
    private AirSpeedAirborneVelocity adsbMessage;

    private double heading;
    private String airspeedType;
    private double airspeed;

    public AirSpeedAirborneVelocityParser(long timeStamp, long highBits, long lowBits) {
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);

        setHeading((int)((ME >>> 42) & 0x01), (int)((ME >>> 32) & 0x3FF));
        setAirspeedType((int)((ME >>> 31) & 0x01));
        setAirspeed((int)((ME >>> 21) & 0x3FF), (int)((ME >>> 48) & 0x7));
    }

    public AirSpeedAirborneVelocity getModeSMessage(){
        this.adsbMessage = new AirSpeedAirborneVelocity();
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
        adsbMessage.setHeading(getHeading());
        adsbMessage.setAirspeedType(getAirspeedType());
        adsbMessage.setAirspeed(getAirspeed());
        return adsbMessage;
    }

    private void setHeading(int status, int value){
        if(status == 1){
            this.heading = (360.0 / 1024) * value;
        }
    }

    private void setAirspeedType(int value){
        switch(value){
            case 0:
                this.airspeedType = "Indicated airspeed (IAS)";
                break;
            case 1:
                this.airspeedType = "True airspeed (TAS)";
                break;
        }
    }
    private void setAirspeed(int value, int subType){
        if(value !=0){
            this.airspeed = value - 1;
            if(subType == 4){
                this.airspeed *= 4;
            }
        }
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public String getAirspeedType() {
        return airspeedType;
    }

    public void setAirspeedType(String airspeedType) {
        this.airspeedType = airspeedType;
    }

    public double getAirspeed() {
        return airspeed;
    }

    public void setAirspeed(double airspeed) {
        this.airspeed = airspeed;
    }
}
