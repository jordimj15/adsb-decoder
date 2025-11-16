package decoder.AdsbParser;

import modes.AdsbMessage;
import modes.raw.AirborneVelocity;

public class AirborneVelocityParser extends AdsbMessageParser {
    private AirborneVelocity adsbMessage;

    private String subType;
    private String intentChangeFlag;
    private int ifrCapabilityFLag;
    private int NACv;
    private String typeVerticalRate;
    private int verticalRate;
    private int altitudeDifference;

    public AirborneVelocityParser(long timeStamp, long highBits, long lowBits){
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);
        setSubType((int)((ME >> 48) & 0x07));
        setIntentChangeFlag((int)((ME >> 47) & 0x01));
        setIfrCapabilityFLag((int)((ME >> 46) & 0x01));
        setNACv((int)((ME >> 43) & 0x07));
        setTypeVerticalRate((int)((ME >> 20) & 0x01));
        setVerticalRate((int)((ME >> 19) & 0x01), (int)((ME >> 10) & 0x1FF));
        setAltitudeDifference((int)((ME >> 7) & 0x01), (int)(ME & 0x7F));

    }

    public AirborneVelocity getModeSMessage(){
        this.adsbMessage = new AirborneVelocity();
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
        return adsbMessage;
    }

    public void setSubType(int value) {
        switch(value) {
            case 1:
                this.subType = "Subsonic ground speed";
                break;
            case 2:
                this.subType = "Supersonic ground speed";
                break;
            case 3:
                this.subType = "Subsonic air speed";
                break;
            case 4:
                this.subType = "Supersonic air speed";
                break;
        }
    }

    public void setIntentChangeFlag(int value) {
        switch(value) {
            case 0:
                this.intentChangeFlag = "No change in Intent";
                break;
            case 1:
                this.intentChangeFlag = "Intent Change";
                break;
        }
    }

    public void setIfrCapabilityFLag(int ifrCapabilityFLag) {
        this.ifrCapabilityFLag = ifrCapabilityFLag;
    }

    public void setNACv(int nACv) {
        this.NACv = nACv;
    }

    public void setTypeVerticalRate(int value) {
        switch(value) {
            case 0:
                this.typeVerticalRate = "GNSS";
                break;
            case 1:
                this.typeVerticalRate = "Barometer";
                break;
        }
    }
    public void setVerticalRate(int sign, int rawVerticalRate) {
        if (rawVerticalRate != 0){
            this.verticalRate = 64 * (rawVerticalRate - 1);
            if(sign == 1){
                this.verticalRate = - this.verticalRate;
            }
        }
    }

    public void setAltitudeDifference(int sign, int rawAltitudeDifference) {
        if(rawAltitudeDifference != 0){
            this.altitudeDifference = 25 * (rawAltitudeDifference - 1);
            if(sign == 1){
                this.altitudeDifference = - this.altitudeDifference;
            }
        }
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getIntentChangeFlag() {
        return intentChangeFlag;
    }

    public void setIntentChangeFlag(String intentChangeFlag) {
        this.intentChangeFlag = intentChangeFlag;
    }

    public int getIfrCapabilityFLag() {
        return ifrCapabilityFLag;
    }

    public int getNACv() {
        return NACv;
    }

    public String getTypeVerticalRate() {
        return typeVerticalRate;
    }

    public void setTypeVerticalRate(String typeVerticalRate) {
        this.typeVerticalRate = typeVerticalRate;
    }

    public int getVerticalRate() {
        return verticalRate;
    }

    public void setVerticalRate(int verticalRate) {
        this.verticalRate = verticalRate;
    }

    public int getAltitudeDifference() {
        return altitudeDifference;
    }

    public void setAltitudeDifference(int altitudeDifference) {
        this.altitudeDifference = altitudeDifference;
    }
}
