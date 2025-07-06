package modes.raw;

import modes.AdsbMessage;

public class AircraftIdentification extends AdsbMessage {
    private int wakeVortexCapability;

    public String getAircraftCategory() {
        return aircraftCategory;
    }

    public void setAircraftCategory(String aircraftCategory) {
        this.aircraftCategory = aircraftCategory;
    }

    private String aircraftCategory;

    public int getWakeVortexCapability() {
        return wakeVortexCapability;
    }

    public void setWakeVortexCapability(int wakeVortexCapability) {
        this.wakeVortexCapability = wakeVortexCapability;
    }
}
