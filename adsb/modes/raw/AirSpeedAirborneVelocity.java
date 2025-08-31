package modes.raw;

public class AirSpeedAirborneVelocity extends AirborneVelocity{
    private Double heading;
    private String airspeedType;
    private double airspeed;

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
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
