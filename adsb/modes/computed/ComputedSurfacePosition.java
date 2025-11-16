package modes.computed;

import modes.raw.SurfacePosition;

public class ComputedSurfacePosition extends ComputedPosition {
    private double movement;
    private int groundTrackStatus;
    private double groundTrack;

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
