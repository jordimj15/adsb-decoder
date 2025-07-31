package modes.raw;

public class GroundSpeedAirborneVelocity extends AirborneVelocity{
    private int velocityEastWest;
    private int velocitySouthWest;
    private double velocityModule;
    private double groundTrack;

    public int getVelocitySouthWest() {
        return velocitySouthWest;
    }

    public void setVelocitySouthWest(int velocitySouthWest) {
        this.velocitySouthWest = velocitySouthWest;
    }

    public int getVelocityEastWest() {
        return velocityEastWest;
    }

    public void setVelocityEastWest(int velocityEastWest) {
        this.velocityEastWest = velocityEastWest;
    }

    public double getVelocityModule() {
        return velocityModule;
    }

    public void setVelocityModule(double velocityModule) {
        this.velocityModule = velocityModule;
    }

    public double getGroundTrack() {
        return groundTrack;
    }

    public void setGroundTrack(double groundTrack) {
        this.groundTrack = groundTrack;
    }
}
