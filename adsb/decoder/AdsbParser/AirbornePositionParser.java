package decoder.AdsbParser;

import modes.raw.AirbornePosition;
import modes.raw.SurfacePosition;

public class AirbornePositionParser extends PositionParser {
    private AirbornePosition adsbMessage;

    private String surveillanceStatus;
    private int nicSubB;
    private String altitudeType;
    private double encodedAltitude;

    public AirbornePositionParser(long timeStamp, long highBits, long lowBits) {
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 40) & bitMasks[24]);
        setSurveillanceStatus((int)((ME >>> 48) & 0b11));
        setNicSubB((int)((ME >>> 47) & 1));
        setAltitudeType((int)(ME >>> 51));
        setEncodedAltitude((int)((ME >>> 35) & 0xFFF));
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

    public double getEncodedAltitude() {
        return encodedAltitude;
    }

    public void setEncodedAltitude(int rawAltitude) {
        if (this.altitudeType == "Barometric altitude") {
            boolean Q = ((rawAltitude >> 7) & 1) == 1;
            if(Q){
                int alt = ((rawAltitude & 0x7F0) >> 1) | (rawAltitude & 0x0F);
                this.encodedAltitude = 25 * alt - 1000;
            }
            else{
                int gray = rawAltitude & 0x7FF; // Remove Q-bit (keep 11 bits)
                int binary = gray;
                for (int mask = gray >> 1; mask != 0; mask >>= 1) {
                    binary ^= mask;
                }
                this.encodedAltitude = binary;
            }
        }else if(this.altitudeType == "GNSS altitude") {
            this.encodedAltitude = rawAltitude;
        }
    }
}
