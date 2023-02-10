package uk.ac.ed.inf;

import java.io.FileWriter;
import java.io.IOException;

public class WriteDrone {
    public static void writeDrone(String date, String droneString) throws IOException {
        String path = "drone-" + date +".geojson";
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(droneString);
        fileWriter.close();
    }
}
