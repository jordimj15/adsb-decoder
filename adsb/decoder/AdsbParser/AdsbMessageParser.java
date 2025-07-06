package decoder.AdsbParser;

import modes.AdsbMessage;

public class AdsbMessageParser {
    public static final long[] bitMasks = new long[64];
    static {
        for (int i = 0; i < 64; i++) {
            bitMasks[i] = (1L << i) - 1;
        }
    }
    private AdsbMessage adsbMessage;

    private long timeStamp;
    private int downlinkFormat;
    private int typeCode;
    private int transponderCapability;
    private String icaoAddress;
    private String callsign;

    public AdsbMessageParser(long timeStamp, long  highBits, long lowBits) {
        int rawIcao24 = (int)((highBits >>> 40) & 0xFFFFFF);
        setIcaoAddress(String.format("%06X", rawIcao24));

        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 40) & bitMasks[24]);
        setTypeCode((int)(ME >>> 51));
        setDownlinkFormat((int)((highBits >>> 59) & 0x1F));
        setTimeStamp(timeStamp);
    }

    public AdsbMessage getModeSMessage(){
        this.adsbMessage = new AdsbMessage();
        adsbMessage.setTimeStamp(getTimeStamp());
        adsbMessage.setDownlinkFormat(getDownlinkFormat());
        adsbMessage.setTypeCode(getTypeCode());
        adsbMessage.setTransponderCapability(getTransponderCapability());
        adsbMessage.setIcaoAddress(getIcaoAddress());

        return adsbMessage;
    }
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
