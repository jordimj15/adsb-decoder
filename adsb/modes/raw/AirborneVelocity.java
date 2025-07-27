package modes.raw;

import modes.AdsbMessage;

public class AirborneVelocity extends AdsbMessage {
    private String subType;
    private int intentChangeFlag;
    private int ifrCapabilityFLag;
    private int NACv;
    private String typeVerticalRate;
    private double verticalRate;
    private String typeAltitudeDifference;
    private double altitudeDifference;
}
