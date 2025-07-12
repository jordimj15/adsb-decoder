package modes.raw;
import modes.AdsbMessage;

public class AirbornePosition extends Position {
    private String surveillanceStatus;
    private int nicSubB;
    private String altitudeType;
    private int encodedAltitude;

    public String getSurveillanceStatus() {
        return surveillanceStatus;
    }

    public void setSurveillanceStatus(String surveillanceStatus) {
        this.surveillanceStatus = surveillanceStatus;
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

    public void setAltitudeType(String altitudeType) {
        this.altitudeType = altitudeType;
    }

    public int getEncodedAltitude() {
        return encodedAltitude;
    }

    public void setEncodedAltitude(int encodedAltitude) {
        this.encodedAltitude = encodedAltitude;
    }
}
