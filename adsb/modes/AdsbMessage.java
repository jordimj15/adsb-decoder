package modes;

public class AdsbMessage {
    private long timeStamp;
    private int downlinkFormat;
    private int typeCode;
    private int transponderCapability;
    private String icaoAddress;
    private String callsign;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getDownlinkFormat() {
        return downlinkFormat;
    }

    public void setDownlinkFormat(int downlinkFormat) {
        this.downlinkFormat = downlinkFormat;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }
    public int getTransponderCapability() {
        return transponderCapability;
    }

    public void setTransponderCapability(int transponderCapability) {
        this.transponderCapability = transponderCapability;
    }

    public String getIcaoAddress() {
        return icaoAddress;
    }

    public void setIcaoAddress(String icaoAddress) {
        this.icaoAddress = icaoAddress;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
}
