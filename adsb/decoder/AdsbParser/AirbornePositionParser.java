package decoder.AdsbParser;

import modes.raw.AirbornePosition;
import modes.raw.SurfacePosition;

public class AirbornePositionParser extends PositionParser {
    private AirbornePosition adsbMessage;

    private String surveillanceStatus;
    private int nicSubB;
    private String altitudeType;
    private int encodedAltitude;

    public AirbornePositionParser(long timeStamp, long highBits, long lowBits) {
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 24) & bitMasks[24]);
        setSurveillanceStatus((int)((ME >>> 48) & 0b11));
        setNicSubB((int)((ME >>> 48) & 1));
        setAltitudeType((int)(ME >>> 51));
        setEncodedAltitude((int)((ME >>> 36) & 0xFFF));
    }

    public AirbornePosition getModeSMessage() {
        this.adsbMessage = new AirbornePosition();
        adsbMessage.setTimeStamp(getTimeStamp());
        adsbMessage.setDownlinkFormat(getDownlinkFormat());
        adsbMessage.setTypeCode(getTypeCode());
        adsbMessage.setTransponderCapability(getTransponderCapability());
        adsbMessage.setIcaoAddress(getIcaoAddress());
        adsbMessage.setCallsign(getCallsign());
        adsbMessage.setSurveillanceStatus(getSurveillanceStatus());
        adsbMessage.setNicSubB(getNicSubB());
        adsbMessage.setAltitudeType(getAltitudeType());
        adsbMessage.setEncodedAltitude(getEncodedAltitude());
        adsbMessage.setCprFormat(getCprFormat());
        adsbMessage.setEncodedLatitude(getEncodedLatitude());
        adsbMessage.setEncodedLongitude(getEncodedLongitude());
        return adsbMessage;
    }

    public String getSurveillanceStatus() {
        return surveillanceStatus;
    }

    public void setSurveillanceStatus(int surveillanceStatus) {
        switch(surveillanceStatus) {
            case 0:
                this.surveillanceStatus = "No condition";
                break;
            case 1:
                this.surveillanceStatus = "Permanent alert";
                break;
            case 2:
                this.surveillanceStatus = "Temporary alert";
                break;
            case 3:
                this.surveillanceStatus = "Permanent warning";
                break;
            default:
                break;
        }
    }

    public int getNicSubB() {
        return nicSubB;
    }

    public void setNicSubB(int nicSubB) {
        this.nicSubB = nicSubB;
    }

    public String getAltitudeType() {
        return altitudeType;
    }

    public void setAltitudeType(int typeCode) {
        boolean isGNSS = typeCode > 18;
        this.altitudeType = isGNSS ? "GNSS altitude" : "Barometric altitude";
    }

    public int getEncodedAltitude() {
        return encodedAltitude;
    }

    public void setEncodedAltitude(int rawAltitude) {
        if ("Barometric altitude".equals(this.altitudeType)) {
            boolean Q = ((rawAltitude >> 4) & 1) == 1;
            if (Q) {
                int high = rawAltitude >> 5;
                int low  = rawAltitude & 0xF;
                int altBits = (high << 4) | low;

                this.encodedAltitude = 25 * altBits - 1000;
            } else {
                int gray = rawAltitude & 0x7FF; // 11 bits
                int binary = gray;
                for (int mask = gray >> 1; mask != 0; mask >>= 1) {
                    binary ^= mask;
                }
                this.encodedAltitude = binary; // (caller probably handles units)
            }
        } else if ("GNSS altitude".equals(this.altitudeType)) {
            this.encodedAltitude = rawAltitude;
        }
    }

}
