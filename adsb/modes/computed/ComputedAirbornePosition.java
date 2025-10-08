package modes.computed;

import modes.raw.AirbornePosition;

public class ComputedAirbornePosition extends ComputedPosition {
    private String surveillanceStatus;
    private int nicSubB;
    private int altitude;
    private String altitudeType;

    public ComputedAirbornePosition(AirbornePosition airbornePosition) {
        setSurveillanceStatus(airbornePosition.getSurveillanceStatus());
        setNicSubB(airbornePosition.getNicSubB());
        setAltitudeType(airbornePosition.getAltitudeType());
        setAltitude(airbornePosition.getEncodedAltitude());
        setTimeStamp(airbornePosition.getTimeStamp());
        setDownlinkFormat(airbornePosition.getDownlinkFormat());
        setTypeCode(airbornePosition.getTypeCode());
        setTransponderCapability(airbornePosition.getTransponderCapability());
        setIcaoAddress(airbornePosition.getIcaoAddress());
        setCallsign(airbornePosition.getCallsign());
    }

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

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public String getAltitudeType() {
        return altitudeType;
    }

    public void setAltitudeType(String altitudeType) {
        this.altitudeType = altitudeType;
    }
}
