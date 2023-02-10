package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;


/**
 * Record representing an area to be used for representing the NoFlyZones.
 *
 * @param name   Name of NoFlyZone representing a String
 * @param coords List of pairs Doubles representing each longitude and latitude coordinate
 *               for the corners of the NoFlyZones
 * @author s2107575
 */
public record Area(@JsonProperty("name") String name,
                   @JsonProperty("coordinates") ArrayList<ArrayList<Double>> coords) {


    /**
     * Method that converts the list of coordinates of the area to a list of Points
     *
     * @return List of Points of coordinates of the area
     */
    public List<Point> getAreaAsPoints() {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < coords.size(); i++) {
            Double lng = coords.get(i).get(0);
            Double lat = coords.get(i).get(1);
            Point asPoint = Point.fromLngLat(lng, lat);
            points.add(asPoint);
        }
        return points;
    }


}
