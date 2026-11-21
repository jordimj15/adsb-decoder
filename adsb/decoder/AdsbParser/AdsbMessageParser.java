package decoder.AdsbParser;

import modes.AdsbMessage;

/**
 * Base parser for ADS-B Extended Squitter messages, which extracts the
 * fields common to every message type.
 *
 * <p>Every parser in this package extends this class. The constructor reads
 * the header fields of the frame (Downlink Format, transponder capability
 * and ICAO address) and the Type Code from the ME field, and each subclass
 * then decodes its own fields from the same ME field. Each parser exposes
 * {@link #getModeSMessage()}, which builds the matching message object from
 * {@code modes} with all the decoded values.
 *
 * <p>The 112-bit frame is received split into two {@code long} values.
 * {@code highBits} holds the first 64 bits of the frame: DF in bits 63-59,
 * CA in bits 58-56, the ICAO address in bits 55-32 and the first 32 bits of
 * ME in bits 31-0. {@code lowBits} holds the remaining 48 bits: the last 24
 * bits of ME in bits 47-24, followed by the 24-bit parity field, which is
 * not checked.
 */
public class AdsbMessageParser {
    /** Lookup table where entry {@code i} is a mask with the {@code i} lowest bits set, shared by all parsers. */
    public static final long[] bitMasks = new long[65];
    static {
        for (int i = 0; i <= 64; i++) {
            bitMasks[i] = (i == 64) ? ~0L : (1L << i) - 1;
        }
    }
    private AdsbMessage adsbMessage;

    private long timeStamp;
    private int downlinkFormat;
    private int typeCode;
    private int transponderCapability;
    private String icaoAddress;
    private String callsign;

    /**
     * Decodes the fields common to every ADS-B message from the raw frame.
     *
     * @param timeStamp the reception time of the message, as a Unix epoch in microseconds
     * @param highBits the first 64 bits of the 112-bit frame
     * @param lowBits the last 48 bits of the 112-bit frame
     */
    public AdsbMessageParser(long timeStamp, long  highBits, long lowBits) {
        int rawIcao24 = (int)((highBits >>> 32) & 0xFFFFFF);
        setIcaoAddress(String.format("%06X", rawIcao24));

        // Rebuild the 56-bit ME field from the last 32 bits of highBits and
        // the first 24 bits of lowBits. The Type Code is its first 5 bits.
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);
        setTypeCode((int)(ME >>> 51));
        setDownlinkFormat((int)((highBits >>> 59) & 0x1F));
        setTimeStamp(timeStamp);
        setTransponderCapability((int)((highBits >>> 56) & 0x07));

    }

    /**
     * Builds a generic {@link AdsbMessage} holding the common fields decoded
     * by this parser.
     *
     * <p>Subclasses override this method to return their own message type,
     * filled in with both the common fields and their type specific ones.
     *
     * @return a new {@link AdsbMessage} with the decoded common fields
     */
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
