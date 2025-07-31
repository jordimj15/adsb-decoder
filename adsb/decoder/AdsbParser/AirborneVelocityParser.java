package decoder.AdsbParser;

import modes.raw.AirborneVelocity;

public class AirborneVelocityParser extends AdsbMessageParser {
    private AirborneVelocity adsbMessage;

    private String subType;
    private int intentChangeFlag;
    private int ifrCapabilityFLag;
    private int NACv;
    private String typeVerticalRate;
    private double verticalRate;
    private String typeAltitudeDifference;
    private double altitudeDifference;

    public AirborneVelocityParser(long timeStamp, long highBits, long lowBits){
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 40) & bitMasks[24]);
        //(int)((ME >> 51) & 0x1F);
        //(int)((ME >> 48) & 0x07);
        //(int)((ME >> 47) & 0x01)
        //(int)((ME >> 46) & 0x01);
        //(int)((ME >> 43) & 0x07);
        //(int)((ME >> 20) & 0x01);
        //(int)((ME >> 19) & 0x01);
        //(int)((ME >> 10) & 0x1FF);
        //(int)((ME >> 7) & 0x01);
        //(int)(ME & 0x7F);
    }

    public void setSubType(int value) {
        switch(value) {
            case 0:
                this.subType = "Subsonic ground speed";
                break;
            case 1:
                this.subType = "Supersonic ground speed";
                break;
            case 2:
                this.subType = "Subsonic air speed";
                break;
            case 3:
                this.subType = "Supersonic air speed";
                break;
        }
    }

    public void setIntentChangeFlag(int intentChangeFlag) {
        this.intentChangeFlag = intentChangeFlag;
    }


}
