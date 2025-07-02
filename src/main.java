import decoder.Decoder;
import modes.AdsbMessage;

public class main {
    public static void main(String[] args) {
        String line = "@1714158001740000*8D0A007958BF00F6BE2D8D5ED65B;";

        Decoder decoder = new Decoder();
        AdsbMessage message = decoder.decode(line);
    }
}
