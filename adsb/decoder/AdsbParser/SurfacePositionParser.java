package decoder.AdsbParser;

import modes.raw.SurfacePosition;

public class SurfacePositionParser extends PositionParser {
    private SurfacePosition adsbMessage;

    private double movement;
    private String groundTrackStatus;
    private double groundTrack;

    public SurfacePositionParser(long timeStamp, long highBits, long lowBits) {
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);

        setMovement((int)((ME >> (56 - 12)) & 0x7F));
        setGroundTrackStatus((int)((ME >> (56 - 13)) & 0x1));
        setGroundTrack((int)((ME >> (56 - 20)) & 0x7F));
    }

    public SurfacePosition getModeSMessage(){
        adsbMessage = new SurfacePosition();
        adsbMessage.setTimeStamp(getTimeStamp());
        adsbMessage.setDownlinkFormat(getDownlinkFormat());
        adsbMessage.setTypeCode(getTypeCode());
        adsbMessage.setTransponderCapability(getTransponderCapability());
        adsbMessage.setIcaoAddress(getIcaoAddress());
        adsbMessage.setCprFormat(getCprFormat());
        adsbMessage.setEncodedLatitude(getEncodedLatitude());
        adsbMessage.setEncodedLongitude(getEncodedLongitude());
        adsbMessage.setGroundTrack(getGroundTrack());
        adsbMessage.setMovement(getMovement());
        adsbMessage.setGroundTrack(getGroundTrack());
        return adsbMessage;
    }

    public double getMovement() {
        return movement;
    }

    public String getGroundTrackStatus() {
        return groundTrackStatus;
    }

    public double getGroundTrack() {
        return groundTrack;
    }

    public void setMovement(int value){
        if (value == 1)
            this.movement = 0.0;
        else if (value >= 2 && value <= 8)
            this.movement = 0.125+(value-2)*0.145833;
        else if (value >= 9 && value <= 12)
            this.movement = 1+(value-9)*0.25;
        else if (value >= 13 && value <= 38)
            this.movement = 2+(value-13)*0.5;
        else if (value >= 39 && value <= 93)
            this.movement = 15 + (value - 39);
        else if (value >= 94 && value <= 108)
            this.movement = 70+(value-94)*2;
        else if (value >= 109 && value <= 123)
            this.movement = 100+(value-109)*5;
        else if (value == 124)
            this.movement = 175.0;

    }
    public void setGroundTrackStatus(int value){
        switch (value){
            case 0:
                this.groundTrackStatus = "Invalid";
                break;
            case 1:
                this.groundTrackStatus = "Valid";
                break;
            default:
                break;
        }
    }
    public void setGroundTrack(int value){
        this.groundTrack = (360.0 / 128) * value;
    }
}
