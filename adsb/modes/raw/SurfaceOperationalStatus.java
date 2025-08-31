package modes.raw;

public class SurfaceOperationalStatus extends OperationalStatus {
    private boolean b2Low;
    private boolean uatIn;
    private int NACv;
    private int NICsupC;
    private double length;
    private double width;
    private int lateralGpsAntennaOffset;
    private int longitudinalGpsAntennaOffset;
    private String trackOrHeading;

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public boolean isB2Low() {
        return b2Low;
    }

    public void setB2Low(boolean b2Low) {
        this.b2Low = b2Low;
    }

    public boolean isUatIn() {
        return uatIn;
    }

    public void setUatIn(boolean uatIn) {
        this.uatIn = uatIn;
    }

    public int getNACv() {
        return NACv;
    }

    public void setNACv(int NACv) {
        this.NACv = NACv;
    }

    public int getNICsupC() {
        return NICsupC;
    }

    public void setNICsupC(int NICsupC) {
        this.NICsupC = NICsupC;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getLongitudinalGpsAntennaOffset() {
        return longitudinalGpsAntennaOffset;
    }

    public void setLongitudinalGpsAntennaOffset(int longitudinalGpsAntennaOffset) {
        this.longitudinalGpsAntennaOffset = longitudinalGpsAntennaOffset;
    }

    public int getLateralGpsAntennaOffset() {
        return lateralGpsAntennaOffset;
    }

    public void setLateralGpsAntennaOffset(int lateralGpsAntennaOffset) {
        this.lateralGpsAntennaOffset = lateralGpsAntennaOffset;
    }

    public String getTrackOrHeading() {
        return trackOrHeading;
    }

    public void setTrackOrHeading(String trackOrHeading) {
        this.trackOrHeading = trackOrHeading;
    }
}
