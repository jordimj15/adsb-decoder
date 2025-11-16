package preprocessor;


import modes.raw.AircraftIdentification;

public class IdMessage {
    private String icao24;
    private String callsign;
    private String aircraftCategory;
    private long lastIdMessageTS;
    private long lastPositionMessageTS;

    public IdMessage (){

    }
    public IdMessage(AircraftIdentification idMsg) {
        this.icao24 = idMsg.getIcaoAddress();
        this.callsign = idMsg.getCallsign();
        this.aircraftCategory = idMsg.getAircraftCategory();
        this.lastIdMessageTS = idMsg.getTimeStamp();
        this.lastPositionMessageTS = 0;
    }

    public String getIcao24() { return icao24; }
    public String getCallsign() { return callsign; }
    public long getLastIdMessageTS() { return lastIdMessageTS; }
    public long getLastPositionMessageTS() { return lastPositionMessageTS; }
    public String getAircraftCategory() {return aircraftCategory;}
    public void setAircraftCategory(String aircraftCategory) {this.aircraftCategory = aircraftCategory;}
    public void setLastIdMessageTS(long ts) { this.lastIdMessageTS = ts; }
    public void setLastPositionMessageTS(long ts) { this.lastPositionMessageTS = ts; }
    public void setCallsign(String cs) { this.callsign = cs; }
}

