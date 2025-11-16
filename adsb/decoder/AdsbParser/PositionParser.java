package decoder.AdsbParser;

import modes.raw.Position;

public class PositionParser extends AdsbMessageParser{
    private Position adsbMessage;

    private String cprFormat;
    private double encodedLatitude;
    private double encodedLongitude;

    public PositionParser(long timeStamp, long highBits, long lowBits){
        super(timeStamp, highBits, lowBits);

        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);

        setEncodedLatitude((int)((ME >>> 17) & 0x1FFFF));
        setEncodedLongitude((int)((ME >>> 0) & 0x1FFFF));
        setCprFormat((int)((ME >> 34) & 1));
    }

    public Position getModeSMessage(){
        this.adsbMessage = new Position();
        adsbMessage.setTimeStamp(getTimeStamp());
        adsbMessage.setDownlinkFormat(getDownlinkFormat());
        adsbMessage.setTypeCode(getTypeCode());
        adsbMessage.setTransponderCapability(getTransponderCapability());
        adsbMessage.setIcaoAddress(getIcaoAddress());
        adsbMessage.setCprFormat(getCprFormat());
        adsbMessage.setEncodedLatitude(getEncodedLatitude());
        adsbMessage.setEncodedLongitude(getEncodedLongitude());
        return adsbMessage;
    }
    public String getCprFormat() {
        return cprFormat;
    }

    public void setCprFormat(int cprFormat) {
        switch (cprFormat) {
            case 0:
                this.cprFormat = "Even";
                break;
            case 1:
                this.cprFormat = "Odd";
                break;
            default:
                break;
        }
    }

    public double getEncodedLatitude() {
        return encodedLatitude;
    }

    public void setEncodedLatitude(int rawLatitude) {
        this.encodedLatitude = rawLatitude / Math.pow(2, 17);
    }

    public double getEncodedLongitude() {
        return encodedLongitude;
    }

    public void setEncodedLongitude(int rawLongitude) {
        this.encodedLongitude = rawLongitude / Math.pow(2, 17);
    }

}
