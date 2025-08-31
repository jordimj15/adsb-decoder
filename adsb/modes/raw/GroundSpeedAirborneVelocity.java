package modes.raw;

public class GroundSpeedAirborneVelocity extends AirborneVelocity{
    private int velocityEastWest;
    private int velocitySouthWest;

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

}
