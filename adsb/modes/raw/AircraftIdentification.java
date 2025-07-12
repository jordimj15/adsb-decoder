package modes.raw;

import modes.AdsbMessage;

public class AircraftIdentification extends AdsbMessage {
    private int wakeVortexCapability;
    private String aircraftCategory;

    public String getAircraftCategory() {
        return aircraftCategory;
    }

    public void setAircraftCategory(String aircraftCategory) {
        this.aircraftCategory = aircraftCategory;
    }

    public int getWakeVortexCapability() {
        return wakeVortexCapability;
    }

    public void setWakeVortexCapability(int wakeVortexCapability) {
        this.wakeVortexCapability = wakeVortexCapability;
    }
}
