package modes.raw;

public class AirborneOperationalStatus extends OperationalStatus {
    private boolean TCASOperational;
    private String ARV;
    private boolean TS;
    private String TC;
    private String uatIn;
    private int GVA;
    private boolean NICbaro;

    public boolean isTCASOperational() {
        return TCASOperational;
    }

    public void setTCASOperational(boolean TCASOperational) {
        this.TCASOperational = TCASOperational;
    }

    public String getARV() {
        return ARV;
    }

    public void setARV(String ARV) {
        this.ARV = ARV;
    }

    public boolean isTS() {
        return TS;
    }

    public void setTS(boolean TS) {
        this.TS = TS;
    }

    public String getTC() {
        return TC;
    }

    public void setTC(String TC) {
        this.TC = TC;
    }

    public String getUatIn() {
        return uatIn;
    }

    public void setUatIn(String uatIn) {
        this.uatIn = uatIn;
    }

    public int getGVA() {
        return GVA;
    }

    public void setGVA(int GVA) {
        this.GVA = GVA;
    }

    public boolean isNICbaro() {
        return NICbaro;
    }

    public void setNICbaro(boolean NICbaro) {
        this.NICbaro = NICbaro;
    }
}
