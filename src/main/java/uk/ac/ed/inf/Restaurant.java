package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.net.URL;


/**
 * Class representing a restaurant participating in the PizzaDronz delivery service as an object.
 *
 * @author fionahamlin
 */
public class Restaurant {
    public static String color;
    public static String symbol;
    private String name;
    private Double lng;
    private Double lat;
    private Menu[] menu;
    private LngLat APPLETON_COORDS = new LngLat(-3.186874, 55.944494);

    private Double distToAppleton = null;

    /**
     * The Restaurant class contains the information from the REST-request to the restaurant endpoint.
     * * Each restaurant object contains its name, location, and menu as given by the REST server.
     * * This class allows you to get a list of all the restaurants from the server and get the menu as a list of
     * {@class Menu} object items of a particular restaurant.
     *
     * @param name name of restaurant as String
     * @param lng  longitude coordinate of restaurant as Double
     * @param lat  latitude coordinate of restaurant as Double
     * @param menu menu of restaurant as list of Menu objects
     */
    public Restaurant(
            @JsonProperty("name") String name,
            @JsonProperty("longitude") Double lng,
            @JsonProperty("latitude") Double lat,
            @JsonProperty("menu") Menu[] menu) {
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        this.menu = menu;
        color = "#000ff";
        symbol = "building";

    }

    /**
     * Static method that returns a list of restaurants as defined by the REST server.
     *
     * @param serverBaseAddress URL of REST-request to restaurant endpoint
     * @return List of Restaurants objects after de-serialization
     * @throws IOException if URL cannot be reached
     */
    public static Restaurant[] getRestaurantsFromRestServer(String serverBaseAddress) throws IOException {
        return new RestClient(new URL(serverBaseAddress)).deserialize("/restaurants", Restaurant[].class);
    }


    /**
     * Method that returns array representation of the menu of a Restaurant object
     *
     * @return ArrayList of @Record Menu classes representing each menu item
     */
    public Menu[] getMenu() {
        return this.menu;
    }

    public LngLat getLngLat() {
        return new LngLat(this.lng, this.lat);
    }

    public Point getPoint() {
        return this.getLngLat().asPoint();
    }

    /**
     * Computes distance from restaurant location to Appleton Tower
     *
     * @return Distance from restaurant to Appleton Tower as Double.
     */
    public Double getDistToAppleton() {
        if (this.distToAppleton == null) {
            this.distToAppleton = this.getLngLat().distanceTo(APPLETON_COORDS);
        }
        return this.distToAppleton;
    }

    public String getName() {
        return this.name;
    }


    @Override
    public String toString() {
        return "name: " + this.name + " lng: " + this.lng + " lat: " + this.lat + " menu: " + this.menu;
    }
}







