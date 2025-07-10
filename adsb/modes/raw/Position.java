package modes.raw;

import modes.AdsbMessage;

public class Position extends AdsbMessage {
    private String cprFormat;
    private double encodedLatitude;
    private double encodedLongitude;

    public String getCprFormat() {
        return cprFormat;
    }

    public void setCprFormat(String cprFormat) {
        this.cprFormat = cprFormat;
    }

    public double getEncodedLatitude() {
        return encodedLatitude;
    }

    public void setEncodedLatitude(double encodedLatitude) {
        this.encodedLatitude = encodedLatitude;
    }

    public double getEncodedLongitude() {
        return encodedLongitude;
    }

    public void setEncodedLongitude(double encodedLongitude) {
        this.encodedLongitude = encodedLongitude;
    }
}
