package modes.raw;

public class AirSpeedAirborneVelocity extends AirborneVelocity{
    private Double heading;
    private int headingStatus;
    private String airspeedType;
    private double airspeed;

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public int getHeadingStatus() {
        return headingStatus;
    }

    public void setHeadingStatus(int headingStatus) {
        this.headingStatus = headingStatus;
    }

    public String getAirspeedType() {
        return airspeedType;
    }

    public void setAirspeedType(String airspeedType) {
        this.airspeedType = airspeedType;
    }

    public double getAirspeed() {
        return airspeed;
    }

    public void setAirspeed(double airspeed) {
        this.airspeed = airspeed;
    }
}
