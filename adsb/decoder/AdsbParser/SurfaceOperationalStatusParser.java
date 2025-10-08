package decoder.AdsbParser;

import modes.raw.OperationalStatus;
import modes.raw.SurfaceOperationalStatus;

public class SurfaceOperationalStatusParser extends OperationalStatusParser {
    private SurfaceOperationalStatus adsbMessage;

    private boolean b2Low;
    private String uatIn;
    private int NACv;
    private int NICsupC;
    private double length;
    private double width;
    private int lateralGpsAntennaOffset;
    private int longitudinalGpsAntennaOffset;
    private String trackOrHeading;

    public SurfaceOperationalStatusParser(long timeStamp, long  highBits, long lowBits) {
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 40) & bitMasks[24]);
        if(getAdsbVersion() == 2){
            setB2Low((int) (ME >> (56 - 14)) & 0b1);
            setUatIn((int) (ME >> (56 - 15)) & 0b1);
            setNACv((int) (ME >> (56 - 18)) & 0b111);
            setNICsupC((int) (ME >> (56 - 19)) & 0b1);
        }
        setLength((int)(ME >> (56 - 22)) & 0b111, (int) ((ME >> (56 - 23)) & 0b1));
        setWidth((int)(ME >> (56 - 23)) & 0b1111);
        setTrkHdg((int)(ME >> (56 - 52)) & 0b1);
        if( ((int)(ME >> (56 - 26)) & 0b11) == 0){
            setLateralGpsAntennaOffset((int)(ME >> (56 - 32)) & 0b1, (int) (ME >> (56 - 34)) & 0b11);
            setLongitudinalGpsAntennaOffset((int) (ME >> (56 - 40)) & 0b111111);
        }
    }

    public SurfaceOperationalStatus getModeSMessage(){
        this.adsbMessage = new SurfaceOperationalStatus();
        adsbMessage.setTimeStamp(getTimeStamp());
        adsbMessage.setDownlinkFormat(getDownlinkFormat());
        adsbMessage.setTypeCode(getTypeCode());
        adsbMessage.setTransponderCapability(getTransponderCapability());
        adsbMessage.setIcaoAddress(getIcaoAddress());
        adsbMessage.setAdsbVersion(getAdsbVersion());
        adsbMessage.setNicSupA(getNICsupA());
        adsbMessage.setNACp(getNACp());
        adsbMessage.setSIL(getSIL());
        adsbMessage.setHorizontalReferenceDirection(getHorizontalReferenceDirection());
        adsbMessage.setSILsup(getSILsup());
        adsbMessage.setB2Low(getB2Low());
        adsbMessage.setUatIn(getUatIn());
        adsbMessage.setNACv(getNACv());
        adsbMessage.setNICsupC(getNICsupC());
        adsbMessage.setLength(getLength());
        adsbMessage.setWidth(getWidth());
        adsbMessage.setLateralGpsAntennaOffset(getLateralGpsAntennaOffset());
        adsbMessage.setLongitudinalGpsAntennaOffset(getLongitudinalGpsAntennaOffset());
        adsbMessage.setTrackOrHeading(getTrackOrHeading());
        return adsbMessage;
    }
    public boolean getB2Low() {
        return b2Low;
    }

    public void setB2Low(int value){
        switch (value){
            case 0:
                this.b2Low = false;
                break;
            case 1:
                this.b2Low = true;
                break;
        }
    }


    public void setUatIn(int value){
        switch (value){
            case 0:
                this.uatIn = "No Capability to Receive ADS-B UAT Messages";
                break;
            case 1:
                this.uatIn = "Aircraft has the Capability to Receive ADS-B UAT Messages";
                break;
        }
    }
    public void setNACv(int nACv) {
        NACv = nACv;
    }

    public void setNICsupC(int nICsupC) {
        NICsupC = nICsupC;
    }

    public void setLength(int lengthValue, int widthValue) {
        if(lengthValue !=0 || widthValue!=0)
             this.length = 15 + 10 * lengthValue;
    }

    public void setWidth(int lengthWidthCode) {
        switch(lengthWidthCode){
            case 1: this.width = 23.0;
                break;
            case 2: this.width = 28.5;
                break;
            case 3: this.width = 34.0;
                break;
            case 4: this.width = 33.0;
                break;
            case 5: this.width = 38.0;
                break;
            case 6: this.width = 39.5;
                break;
            case 7: this.width = 45.0;
                break;
            case 8: this.width = 45.0;
                break;
            case 9: this.width = 52.0;
                break;
            case 10: this.width = 59.5;
                break;
            case 11: this.width = 67.0;
                break;
            case 12: this.width = 72.5;
                break;
            case 13: this.width = 80.0;
                break;
            case 14: this.width = 80.0;
                break;
            case 15: this.width = 90.0;
                break;
            default:
                break;
        }
    }

    public void setLateralGpsAntennaOffset(int direction, int value) {
        if(direction==0)
            this.lateralGpsAntennaOffset = -2*value;
        else
            this.lateralGpsAntennaOffset = 2*value;
    }

    public void setLongitudinalGpsAntennaOffset(int value) {

        this.longitudinalGpsAntennaOffset = 2*(value-1);
    }

    public void setTrkHdg(int value) {
        switch(value){
            case 0:
                this.trackOrHeading = "Track Angle";
                break;
            case 1:
                this.trackOrHeading = "Heading";
                break;
        }
    }

    public SurfaceOperationalStatus getAdsbMessage() {
        return adsbMessage;
    }

    public boolean isB2Low() {
        return b2Low;
    }

    public String getUatIn() {
        return uatIn;
    }

    public int getNACv() {
        return NACv;
    }

    public int getNICsupC() {
        return NICsupC;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public int getLateralGpsAntennaOffset() {
        return lateralGpsAntennaOffset;
    }

    public int getLongitudinalGpsAntennaOffset() {
        return longitudinalGpsAntennaOffset;
    }

    public String getTrackOrHeading() {
        return trackOrHeading;
    }
}
