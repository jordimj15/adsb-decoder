package preprocessor;


import modes.raw.AircraftIdentification;

/**
 * Identification data of one aircraft, kept by the {@link Preprocessor} to
 * attach a callsign and aircraft category to the rest of its messages.
 *
 * <p>An entry is created from the first Aircraft Identification message
 * received for an ICAO address, and its callsign and last identification
 * timestamp are updated with every later one. The {@link Preprocessor}
 * discards the entry once no identification has been received for that
 * aircraft within its configured time limit.
 *
 * <p>All timestamps are Unix epochs in microseconds.
 */
public class IdMessage {
    private String icao24;
    private String callsign;
    private String aircraftCategory;
    /** Timestamp of the last Aircraft Identification message received for this aircraft. */
    private long lastIdMessageTS;
    private long lastPositionMessageTS;

    public IdMessage (){

    }

    /**
     * Builds an identification entry from a decoded Aircraft Identification message.
     *
     * @param idMsg the Aircraft Identification message to take the ICAO address, callsign, category and timestamp from
     */
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

