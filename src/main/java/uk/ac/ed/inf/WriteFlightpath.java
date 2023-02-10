package uk.ac.ed.inf;

import com.mapbox.geojson.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteFlightpath {
    private static JSONArray flightpath(List<String> moveOrders, List<Point> fp, List<String> angles, List<Integer> ticks, Integer start){
        JSONArray flightpathArray = new JSONArray();

        for (int i = 0; i < fp.size() - 1; i++) {
            JSONObject myflightpath = new JSONObject();
            myflightpath.put("orderNo", moveOrders.get(i));
            myflightpath.put("fromLongitude", fp.get(i).longitude());
            myflightpath.put("fromLatitude", fp.get(i).latitude());
            myflightpath.put("angle", angles.get(i));
            myflightpath.put("toLongitude", fp.get(i + 1).longitude());
            myflightpath.put("toLatitude", fp.get(i + 1).latitude());
            Integer sinceCalc = ticks.get(i) - start;
            myflightpath.put("ticksSinceStartOfCalculation", sinceCalc);
            flightpathArray.add(myflightpath);
        }
        return flightpathArray;
    }

    public static void writeFile(String date, List<String> moveOrders, List<Point> flightpath, List<String> angles,
                                 List<Integer> ticks, Integer start) throws IOException {
        String path = "flightpath-" + date +".json";
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(flightpath(moveOrders, flightpath, angles, ticks, start).toJSONString());
        fileWriter.close();
    }

}
