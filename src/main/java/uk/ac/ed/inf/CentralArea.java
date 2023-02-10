package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Singleton class representing University of Edinburgh Central Area
 *
 * @author S2107575
 */
public final class CentralArea {
    private static final String DEFAULT_ENDPOINT = "https://ilp-rest.azurewebsites.net";
    public static String name;
    private static CentralArea centralAreaInstance;
    private static List<LngLat> coords;
    private static List<Point> points = new ArrayList<>();
    private URL baseURL;

    /**
     * Creates single instance of CentralArea if it has not already been created, otherwise returns current instance.
     *
     * @return CentralArea instance
     * @throws IOException If input or output error occurs when trying to access REST server in CentralArea constructor
     */
    public static synchronized CentralArea getCentralAreaInstance(String baseEndpoint) {
        if (centralAreaInstance == null) {
            centralAreaInstance = new CentralArea();
            name = "Central Area";
        }
        try {
            centralAreaInstance.baseURL = new URL(baseEndpoint);
        } catch (MalformedURLException ignore) {
            try {
                centralAreaInstance.baseURL = new URL(DEFAULT_ENDPOINT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.exit(2);
            }
        }
        return centralAreaInstance;
    }

    /**
     * Retrieves the coordinates for the CentralArea object by accessing the REST server.
     */

    public void retrieveCentralAreaCoords() {
        LngLat[] myCords = new RestClient(baseURL).deserialize("/centralArea", LngLat[].class);
        coords = Arrays.asList(myCords);
    }

    /**
     * Gets the Central Area as represented by GeoJson Point coordinates.
     *
     * @return List of Point Objects representing the CentralArea.
     */
    public List<Point> getCentralAreaPoints() {
        if (points.isEmpty()) {
            for (LngLat coord : coords) {
                points.add(coord.asPoint());
            }
        }
        return points;
    }


    /**
     * Gets the Central Area as represented by LngLat objects.
     *
     * @return List of LngLat objects representing the coordinates.
     */
    public List<LngLat> getCoords() {
        if (coords.isEmpty()) {
            retrieveCentralAreaCoords();
        }
        return coords;
    }


}
