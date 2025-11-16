package decoder.AdsbParser;

import modes.raw.OperationalStatus;

public class OperationalStatusParser extends AdsbMessageParser{
    private OperationalStatus adsbMessage;

    private int adsbVersion;
    private int NICsupA;
    private int NACp;
    private int SIL;
    private String horizontalReferenceDirection;
    private String SILsup;

    public OperationalStatusParser(long timeStamp, long  highBits, long lowBits){
        super(timeStamp, highBits, lowBits);

        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);
        setAdsbVersion((int)((ME >> (56 - 43)) & 0x07));
        setNicsupA((int)((ME >> (56 - 44)) & 0x01));
        setNacp((int)((ME >> (56 - 48)) & 0x0F));
        setSIL ((int)((ME >> (56 - 52)) & 0x03));
        setHorizontalReferenceDirection((int)((ME >> (56 - 55)) & 0x01));
        setSILsup((int)((ME >> (56 - 56)) & 0x01));
    }

    public OperationalStatus getModeSMessage(){
        this.adsbMessage = new OperationalStatus();
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
        return adsbMessage;
    }
    public void setAdsbVersion(int adsbVersion){
        if (adsbVersion > 0 && adsbVersion < 3){
            this.adsbVersion = adsbVersion;
        }
    }
    public void setNicsupA(int nicsupA){
        this.NICsupA = nicsupA;
    }
    public void setNacp(int nacp){
        this.NACp = nacp;
    }

    public void setSIL(int sil){
        this.SIL = sil;
    }

    public void setHorizontalReferenceDirection(int value){
        switch (value){
            case 0:
                this.horizontalReferenceDirection = "True North";
                break;
            case 1:
                this.horizontalReferenceDirection = "Magnetic North";
                break;
        }
    }
    public void setSILsup(int value){
        switch (value){
            case 0:
                this.SILsup = "Per hour";
                break;
            case 1:
                this.SILsup = "Per minute";
                break;
        }
    }

    public int getAdsbVersion() {
        return adsbVersion;
    }

    public int getNICsupA() {
        return NICsupA;
    }

    public int getNACp() {
        return NACp;
    }

    public int getSIL() {
        return SIL;
    }

    public String getHorizontalReferenceDirection() {
        return horizontalReferenceDirection;
    }

    public String getSILsup() {
        return SILsup;
    }
}
