package decoder.AdsbParser;

import modes.raw.AirborneOperationalStatus;
import modes.raw.OperationalStatus;
import modes.raw.SurfaceOperationalStatus;

public class AirborneOperationalStatusParser extends OperationalStatusParser {
    private AirborneOperationalStatus adsbMessage;

    private boolean TCASOperational;
    private String ARV;
    private boolean TS;
    private String TC;
    private String uatIn;
    private int GVA;
    private boolean NICbaro;

    public AirborneOperationalStatusParser(long timeStamp, long  highBits, long lowBits) {
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 40) & bitMasks[24]);
        if(getAdsbVersion() == 2){
            setTCASOperational((int) ((ME >>> (56 - 14)) & 0b1));
            setARV((int) ((ME >> (56 - 15)) & 0b1));
            setTS((int) ((ME >> (56 - 18)) & 0b111));
            setTC((int) ((ME >>> (56 - 18)) & 0b111));
            setUatIn((int) ((ME >>> (56 - 19)) & 0b1));
        }
        setGVA((int) ((ME >>> (56 - 50)) & 0b11));
        setNICbaro((int) ((ME >>> (56 - 53)) & 0b1));
    }
    public AirborneOperationalStatus getAdsbMessage() {
        this.adsbMessage = new AirborneOperationalStatus();
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
        adsbMessage.setTCASOperational(isTCASOperational());
        adsbMessage.setARV(getARV());
        adsbMessage.setTS(isTS());
        adsbMessage.setTC(getTC());
        adsbMessage.setUatIn(getUatIn());
        adsbMessage.setGVA(getGVA());
        adsbMessage.setNICbaro(isNICbaro());
        return this.adsbMessage;
    }

    public void setTCASOperational(int value){
        switch(value){
            case 0:
                this.TCASOperational = false;
                break;
            case 1:
                this.TCASOperational = true;
                break;
            default:
                break;
        }
    }
    public void setARV(int value) {
        switch(value){
            case 0: this.ARV = "No capability for sending messages to support Air Referenced Velocity Reports";
                break;
            case 1: this.ARV = "Capability of sending messages to support Air-Referenced Velocity Reports.";
                break;
            default:
                break;
        }
    }
    public void setTS(int value) {
        switch(value){
            case 0: this.TS = false;
                break;
            case 1: this.TS = true;
                break;
            default:
                break;
        }
    }
    public void setTC(int value){
        switch(value){
            case 0:
                this.TC = "No capability for sending messages to support Trajectory Change Reports";
                break;
            case 1:
                this.TC = "Capability of sending messages to support TC+0 Report only.";
                break;
            case 2:
                this.TC = "Capability of sending information for multiple TC reports.";
                break;
            case 3:
                this.TC = "(Reserved for future use.)";
                break;
            default:
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
    public void setGVA(int GVA){
        this.GVA = GVA;
    }

    public void setNICbaro(int value) {
        switch(value){
            case 0: this.TS = false;
                break;
            case 1: this.TS = true;
                break;
            default:
                break;
        }
    }

    public boolean isTCASOperational() {
        return TCASOperational;
    }

    public String getARV() {
        return ARV;
    }

    public boolean isTS() {
        return TS;
    }

    public String getTC() {
        return TC;
    }

    public String getUatIn() {
        return uatIn;
    }

    public int getGVA() {
        return GVA;
    }

    public boolean isNICbaro() {
        return NICbaro;
    }
}
