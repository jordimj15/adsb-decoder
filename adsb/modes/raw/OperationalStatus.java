package modes.raw;

import modes.AdsbMessage;

public class OperationalStatus extends AdsbMessage {
    private int adsbVersion;
    private int nicSupA;
    private int NACp;
    private int SIL;
    private String horizontalReferenceDirection;
    private String SILsup;
    private boolean activeTCASRA;
    private boolean identSwitchActive;
    private boolean receivingATCServices;
    private boolean singleAntennaFlag;
    private int systemDesignAssurance;


    public int getAdsbVersion() {
        return adsbVersion;
    }

    public void setAdsbVersion(int adsbVersion) {
        this.adsbVersion = adsbVersion;
    }

    public int getNicSupA() {
        return nicSupA;
    }

    public void setNicSupA(int nicSupA) {
        this.nicSupA = nicSupA;
    }

    public int getNACp() {
        return NACp;
    }

    public void setNACp(int NACp) {
        this.NACp = NACp;
    }

    public int getSIL() {
        return SIL;
    }

    public void setSIL(int SIL) {
        this.SIL = SIL;
    }

    public String getHorizontalReferenceDirection() {
        return horizontalReferenceDirection;
    }

    public void setHorizontalReferenceDirection(String horizontalReferenceDirection) {
        this.horizontalReferenceDirection = horizontalReferenceDirection;
    }

    public String getSILsup() {
        return SILsup;
    }

    public void setSILsup(String SILsup) {
        this.SILsup = SILsup;
    }

    public boolean isActiveTCASRA() {
        return activeTCASRA;
    }

    public void setActiveTCASRA(boolean activeTCASRA) {
        this.activeTCASRA = activeTCASRA;
    }

    public boolean isIdentSwitchActive() {
        return identSwitchActive;
    }

    public void setIdentSwitchActive(boolean identSwitchActive) {
        this.identSwitchActive = identSwitchActive;
    }

    public boolean isReceivingATCServices() {
        return receivingATCServices;
    }

    public void setReceivingATCServices(boolean receivingATCServices) {
        this.receivingATCServices = receivingATCServices;
    }

    public boolean isSingleAntennaFlag() {
        return singleAntennaFlag;
    }

    public void setSingleAntennaFlag(boolean singleAntennaFlag) {
        this.singleAntennaFlag = singleAntennaFlag;
    }

    public int getSystemDesignAssurance() {
        return systemDesignAssurance;
    }

    public void setSystemDesignAssurance(int systemDesignAssurance) {
        this.systemDesignAssurance = systemDesignAssurance;
    }
}
