package modes.raw;

import modes.AdsbMessage;

public class AirborneVelocity extends AdsbMessage {
    private String subType;
    private String intentChangeFlag;
    private int ifrCapabilityFLag;
    private int NACv;
    private String typeVerticalRate;
    private Double verticalRate;
    private Double altitudeDifference;

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
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

    public String getIntentChangeFlag() {
        return intentChangeFlag;
    }

    public void setIntentChangeFlag(String intentChangeFlag) {
        this.intentChangeFlag = intentChangeFlag;
    }

    public Double getVerticalRate() {
        return verticalRate;
    }

    public void setVerticalRate(Double verticalRate) {
        this.verticalRate = verticalRate;
    }

    public Double getAltitudeDifference() {
        return altitudeDifference;
    }

    public void setAltitudeDifference(Double altitudeDifference) {
        this.altitudeDifference = altitudeDifference;
    }
}
