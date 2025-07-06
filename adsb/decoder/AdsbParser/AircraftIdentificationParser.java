package decoder.AdsbParser;

import modes.raw.AircraftIdentification;

public class AircraftIdentificationParser extends AdsbMessageParser {

    private AircraftIdentification adsbMessage;
    private String aircraftCategory;
    private int wakeVortexCapability;



    public AircraftIdentificationParser(long timeStamp, long highBits, long lowBits){
        super(timeStamp, highBits, lowBits);
        long ME = ((highBits & bitMasks[32]) << 24) | ((lowBits >>> 40) & bitMasks[24]);
        int CA = (int)((ME >>> 56) & 0x07);
        setWakeVortexCapability(CA);
        int TC = (int)(ME >>> 51);
        setAircraftCategory(TC, CA);
    }

    public AircraftIdentification getModeSMessage(long timeStamp, long ME){
        adsbMessage = new AircraftIdentification();
        adsbMessage.setTimeStamp(getTimeStamp());
        adsbMessage.setDownlinkFormat(getDownlinkFormat());
        adsbMessage.setTypeCode(getTypeCode());
        adsbMessage.setTransponderCapability(getTransponderCapability());
        adsbMessage.setIcaoAddress(getIcaoAddress());
        adsbMessage.setCallsign(getCallsign());
        adsbMessage.setWakeVortexCapability(getWakeVortexCapability());
        adsbMessage.setAircraftCategory(getAircraftCategory());

        return adsbMessage;
    }

    public void setAircraftCategory(int TC, int CA){
        switch (TC){
            case 1: this.aircraftCategory = "Reserved";
                break;
            case 2:
                switch (CA){
                    case 0: this.aircraftCategory = "No category information";
                        break;
                    case 1: this.aircraftCategory = "Surface emergency vehicle";
                        break;
                    case 3: this.aircraftCategory = "Surface service vehicle";
                        break;
                    case 4: case 5: case 6: case 7: this.aircraftCategory = "Ground obstruction";
                        break;
                }
                break;
            case 3:
                switch (CA){
                    case 0: this.aircraftCategory = "No category information";
                        break;
                    case 1: this.aircraftCategory = "Glider, sailplane";
                        break;
                    case 2: this.aircraftCategory = "Lighter-than-air";
                        break;
                    case 3: this.aircraftCategory = "Parachutist, skydiver";
                        break;
                    case 4: this.aircraftCategory = "Ultralight, hang-glider, paraglider";
                        break;
                    case 5: this.aircraftCategory = "Reserved";
                        break;
                    case 6: this.aircraftCategory = "Unmanned aerial vehicle";
                        break;
                    case 7: this.aircraftCategory = "Space or transatmospheric vehicle";
                        break;
                }
                break;
            case 4:
                switch (CA){
                    case 0: this.aircraftCategory = "No category information";
                        break;
                    case 1: this.aircraftCategory = "Light (less than 7000 kg)";
                        break;
                    case 2: this.aircraftCategory = "Medium 1 (between 7000 kg and 34000 kg)";
                        break;
                    case 3: this.aircraftCategory = "Medium 2 (between 34000 kg to 136000 kg)";
                        break;
                    case 4: this.aircraftCategory = "High vortex aircraft";
                        break;
                    case 5: this.aircraftCategory = "Heavy (larger than 136000 kg)";
                        break;
                    case 6: this.aircraftCategory = "High performance (>5 g acceleration) and high speed (>400 kt)";
                        break;
                    case 7: this.aircraftCategory = "Rotorcraft";
                        break;
                }
                break;
        }
    }

    public String setCallsign(long ME){
        final String charMapping = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ#####_###############0123456789######";
        char[] callsign = new char[8];
        for (int i = 0; i < 8; i++) {
            int bitPos = 56 - ((i + 1) * 6);
            int sixBits = (int)((ME >>> bitPos) & 0x3F);
            callsign[i] = charMapping.charAt(sixBits);
        }
        return new String(callsign).replace("#","").replace("_","");
    }

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
