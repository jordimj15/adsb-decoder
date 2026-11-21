import decoder.Decoder;
import modes.AdsbMessage;
import modes.computed.ComputedAirbornePosition;
import modes.computed.ComputedPosition;
import modes.computed.ComputedSurfacePosition;
import modes.raw.*;
import preprocessor.Preprocessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.*;

public class main {
    public static void main(String[] args) throws Exception {
        String fileContent = readFile("src/TestFile");
        String[] fileLines = fileContent.split("\n");

        Decoder decoder = new Decoder();
        Preprocessor preprocessor = new Preprocessor();
        List<AdsbMessage> messages = new ArrayList<>();

        for (String line : fileLines) {
            if (!line.startsWith("")) {}
            AdsbMessage message = decoder.decode(line);
            if( message != null){
                List<AdsbMessage> processedMessages = preprocessor.preprocess(message);
                if (processedMessages.size() > 0) {
                    messages.addAll(processedMessages);
                }
            }
        }

        int contAirbornePosition = 0;
        int contSurfacePosition = 0;
        int contAirSpeedAirborneVelocity = 0;
        int contGroundSpeedAirborneVelocity = 0;
        int contAirborneOperationalStatus = 0;
        int contSurfaceOperationalStatus = 0;

        List<ComputedPosition> allPositions = new ArrayList<>();
        for (AdsbMessage msg : messages) {
            switch (msg) {
                case ComputedAirbornePosition ap -> contAirbornePosition++;
                case ComputedSurfacePosition sp -> contSurfacePosition++;
                case AirSpeedAirborneVelocity av  -> contAirSpeedAirborneVelocity++;
                case GroundSpeedAirborneVelocity av -> contGroundSpeedAirborneVelocity++;
                case AirborneOperationalStatus aos -> contAirborneOperationalStatus++;
                case SurfaceOperationalStatus sos -> contSurfaceOperationalStatus++;
                default -> {}
            }
            if(msg instanceof ComputedPosition){
                allPositions.add((ComputedPosition) msg);
            }
        }
        System.out.println("Airborne position count: " + contAirbornePosition);
        System.out.println("Surface position count: " + contSurfacePosition);
        System.out.println("Airborne operational count: " + contAirborneOperationalStatus);
        System.out.println("Surface operational count: " + contSurfaceOperationalStatus);
        System.out.println("Ground speed count: " + contGroundSpeedAirborneVelocity);
        System.out.println("Air speed count: " + contAirSpeedAirborneVelocity);
        Map<String, List<ComputedPosition>> positionsByIcao =
                allPositions.stream()
                        .collect(Collectors.groupingBy(ComputedPosition::getIcaoAddress));
    }
    public static String readFile(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(64 * 1024);
        char[] buf = new char[8192];

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int numRead;
            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }
        }

        return fileData.toString();
    }
}
