package modes;

/**
 * Base class for every decoded ADS-B message, holding the fields shared by
 * all message types.
 *
 * <p>The header fields (timestamp, Downlink Format, Type Code, transponder
 * capability and ICAO address) are filled in by the parser when the message
 * is decoded. The callsign and aircraft category are only transmitted in
 * Aircraft Identification messages, so for every other message type they are
 * attached later by the {@code Preprocessor}, taken from the last
 * identification received for the same ICAO address.
 */
public class AdsbMessage {
    /** Reception time of the message, as a Unix epoch in microseconds. */
    private long timeStamp;
    private int downlinkFormat;
    private int typeCode;
    private int transponderCapability;
    /** 24-bit ICAO address of the aircraft, as six uppercase hexadecimal digits. */
    private String icaoAddress;
    private String callsign;
    private String aircraftCategory;

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

    public String getAircraftCategory() {
        return aircraftCategory;
    }

    public void setAircraftCategory(String aircraftCategory) {
        this.aircraftCategory = aircraftCategory;
    }
}
