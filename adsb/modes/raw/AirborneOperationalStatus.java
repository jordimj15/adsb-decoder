package modes.raw;

public class AirborneOperationalStatus extends OperationalStatus {
    private boolean TCASOperational;
    private boolean ARV;
    private boolean TS;
    private String TC;
    private boolean uatIn;
    private int GVA;
    private boolean NICbaro;

    public boolean isTCASOperational() {
        return TCASOperational;
    }

    public void setTCASOperational(boolean TCASOperational) {
        this.TCASOperational = TCASOperational;
    }

    public boolean isARV() {
        return ARV;
    }

    public void setARV(boolean ARV) {
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

    public boolean isUatIn() {
        return uatIn;
    }

    public void setUatIn(boolean uatIn) {
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
