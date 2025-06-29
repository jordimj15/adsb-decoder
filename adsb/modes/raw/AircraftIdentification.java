package modes.raw;

import modes.AdsbMessage;

public class AircraftIdentification extends AdsbMessage {
    private int category;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
