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

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public int getIntentChangeFlag() {
        return intentChangeFlag;
    }

    public void setIntentChangeFlag(int intentChangeFlag) {
        this.intentChangeFlag = intentChangeFlag;
    }

    public int getIfrCapabilityFLag() {
        return ifrCapabilityFLag;
    }

    public void setIfrCapabilityFLag(int ifrCapabilityFLag) {
        this.ifrCapabilityFLag = ifrCapabilityFLag;
    }

    public int getNACv() {
        return NACv;
    }

    public void setNACv(int NACv) {
        this.NACv = NACv;
    }

    public String getTypeVerticalRate() {
        return typeVerticalRate;
    }

    public void setTypeVerticalRate(String typeVerticalRate) {
        this.typeVerticalRate = typeVerticalRate;
    }

    public double getVerticalRate() {
        return verticalRate;
    }

    public void setVerticalRate(double verticalRate) {
        this.verticalRate = verticalRate;
    }

    public String getTypeAltitudeDifference() {
        return typeAltitudeDifference;
    }

    public void setTypeAltitudeDifference(String typeAltitudeDifference) {
        this.typeAltitudeDifference = typeAltitudeDifference;
    }

    public double getAltitudeDifference() {
        return altitudeDifference;
    }

    public void setAltitudeDifference(double altitudeDifference) {
        this.altitudeDifference = altitudeDifference;
    }
}
