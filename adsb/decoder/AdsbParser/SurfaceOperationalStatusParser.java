package decoder.AdsbParser;

import modes.raw.SurfaceOperationalStatus;

public class SurfaceOperationalStatusParser extends OperationalStatusParser {
    private SurfaceOperationalStatus adsbMessage;

    private boolean b2Low;
    private boolean uatIn;
    private int NACv;
    private int NICsupC;
    private double length;
    private double width;
    private int lateralGpsAntennaOffset;
    private int longitudinalGpsAntennaOffset;
    private String trackOrHeading;

    public SurfaceOperationalStatusParser(long timeStamp, long  highBits, long lowBits) {
        super(timeStamp, highBits, lowBits);
        
    }
}
