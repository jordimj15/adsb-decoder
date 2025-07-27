package modes.raw;

import modes.AdsbMessage;

public class SurfacePosition extends Position {
    private Double movement;
    private int groundTrackStatus;
    private double groundTrack;

    public Double getMovement() {
        return movement;
    }

    public void setMovement(Double movement) {
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
