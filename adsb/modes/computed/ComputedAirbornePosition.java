package modes.computed;

import modes.raw.AirbornePosition;

/**
 * Airborne position with its coordinates resolved by global CPR decoding.
 *
 * <p>It is built from the newer of the two paired {@link AirbornePosition}
 * messages, from which it takes the header fields and the airborne specific
 * ones (surveillance status, NIC-sup-B, altitude and altitude type). The
 * latitude and longitude are set afterwards by the {@code Preprocessor}.
 *
 * <p>{@code altitude} is the value decoded by {@code AirbornePositionParser},
 * and {@code altitudeType} tells whether it is a barometric or a GNSS
 * altitude.
 */
public class ComputedAirbornePosition extends ComputedPosition {
    private String surveillanceStatus;
    private int nicSubB;
    private int altitude;
    private String altitudeType;

    /**
     * Builds a computed airborne position from a raw airborne position,
     * copying its header fields, callsign and airborne specific fields. The
     * CPR encoded coordinates are not copied.
     *
     * @param airbornePosition the raw airborne position this position is based on
     */
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
