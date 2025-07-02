package decoder.AdsbParser;

import modes.raw.AircraftIdentification;

public class AircraftIdentificationParser {
    private AircraftIdentification adsbMessage;
    private String aircraftCategory;
    private String emitterCategoryLetter;

    public AircraftIdentificationParser(long timeStamp, long ME, int TC){
        int CA = (int)((ME >>> 56) & 0x07);
        setAircraftCategory(TC, CA);
    }

    public void setAircraftCategory(int TC, int CA){
        switch (TC){
            case 1: this.aircraftCategory = "Reserved";
                break;
            case 2:
                switch (CA){
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
}
