package modes.raw;

import modes.AdsbMessage;

public class SurfacePosition extends Position {
    private double movement;
    private int groundTrackStatus;
    private double groundTrack;
    private int time;

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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
