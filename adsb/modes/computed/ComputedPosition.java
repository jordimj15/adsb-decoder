package modes.computed;

import modes.AdsbMessage;

/**
 * Base class for a position resolved by the {@code Preprocessor}, holding
 * the actual latitude and longitude of the aircraft.
 *
 * <p>Raw position messages ({@code modes.raw.Position}) only carry CPR
 * encoded coordinates, which cannot be turned into a position on their own.
 * Once an even and an odd message from the same aircraft are paired, global
 * CPR decoding gives the real coordinates, which are stored here.
 *
 * <p>The subclasses {@link ComputedAirbornePosition} and
 * {@link ComputedSurfacePosition} add the fields specific to each kind of
 * position.
 */
public class ComputedPosition extends AdsbMessage {
    /** Decoded latitude, in decimal degrees. */
    public double computedLatitude;
    /** Decoded longitude, in decimal degrees. */
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
