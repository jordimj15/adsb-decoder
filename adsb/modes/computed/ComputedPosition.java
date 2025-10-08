package modes.computed;

import modes.AdsbMessage;

public class ComputedPosition extends AdsbMessage {
    public double computedLatitude;
    public double computedLongitude;

    public double getComputedLatitude() {
        return computedLatitude;
    }

    public void setComputedLatitude(double computedLatitude) {
        this.computedLatitude = computedLatitude;
    }

    public double getComputedLongitude() {
        return computedLongitude;
    }

    public void setComputedLongitude(double computedLongitude) {
        this.computedLongitude = computedLongitude;
    }
}
