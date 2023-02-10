package uk.ac.ed.inf;


import java.net.MalformedURLException;
import java.net.URL;


/**
 * Class representing the NoFlyZones of the PizzaDronz service.
 *
 * @author s2107575
 */
public final class NoFlyZones {
    private static final String DEFAULT_ENDPOINT = "https://ilp-rest.azurewebsites.net";
    public static String fill = "#ff000";
    private static NoFlyZones noFlyZonesInstance;
    private static Area[] noFlyAreas;
    private URL baseURL;


    /**
     * Creates single instance of NoFlyZones if it has not already been created, otherwise returns current instance.
     *
     * @param baseEndpoint Base endpoint for service to REST server.
     * @return NoFlyZones instance
     */
    public static synchronized NoFlyZones getNoFlyZonesInstance(String baseEndpoint) {
        if (noFlyZonesInstance == null) {
            noFlyZonesInstance = new NoFlyZones();
        }
        try {
            noFlyZonesInstance.baseURL = new URL(baseEndpoint);
        } catch (MalformedURLException ignore) {
            try {
                noFlyZonesInstance.baseURL = new URL(DEFAULT_ENDPOINT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.exit(2);
            }
        }
        return noFlyZonesInstance;
    }


    /**
     * Retrieves the coordinates for the NoFLyZones by accessing the REST server.
     *
     * @return The NoFlyZones represented by Area objects.
     */
    public Area[] getNoFlyZoneCoordinates() {
        if (noFlyAreas == null) {
            Area[] areas = new RestClient(baseURL).deserialize("/noFlyZones", Area[].class);
            noFlyAreas = areas;
        }
        return noFlyAreas;
    }

}
