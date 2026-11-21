package modes.computed;

import modes.raw.SurfacePosition;

/**
 * Surface position with its coordinates resolved by global CPR decoding.
 *
 * <p>It is built from the newer of the two paired {@link SurfacePosition}
 * messages, from which it takes the header fields and the surface specific
 * ones (movement, ground track status and ground track). The latitude and
 * longitude are set afterwards by the {@code Preprocessor}.
 */
public class ComputedSurfacePosition extends ComputedPosition {
    /** Ground speed of the aircraft, in knots. */
    private double movement;
    private int groundTrackStatus;
    /** Ground track of the aircraft, in degrees clockwise from north. */
    private double groundTrack;

    /**
     * Builds a computed surface position from a raw surface position,
     * copying its header fields, callsign and surface specific fields. The
     * CPR encoded coordinates are not copied.
     *
     * @param surfacePosition the raw surface position this position is based on
     */
    public ComputedSurfacePosition(SurfacePosition surfacePosition) {
        setMovement(surfacePosition.getMovement());
        setGroundTrackStatus(surfacePosition.getGroundTrackStatus());
        setGroundTrack(surfacePosition.getGroundTrack());
        setTimeStamp(surfacePosition.getTimeStamp());
        setDownlinkFormat(surfacePosition.getDownlinkFormat());
        setTypeCode(surfacePosition.getTypeCode());
        setTransponderCapability(surfacePosition.getTransponderCapability());
        setIcaoAddress(surfacePosition.getIcaoAddress());
        setCallsign(surfacePosition.getCallsign());
    }
    public double getMovement() {
        return movement;
    }

    public void setMovement(double movement) {
        this.movement = movement;
    }

    public int getGroundTrackStatus() {
        return groundTrackStatus;
    }

    public void setGroundTrackStatus(int groundTrackStatus) {
        this.groundTrackStatus = groundTrackStatus;
    }

    public double getGroundTrack() {
        return groundTrack;
    }

    public void setGroundTrack(double groundTrack) {
        this.groundTrack = groundTrack;
    }
}
