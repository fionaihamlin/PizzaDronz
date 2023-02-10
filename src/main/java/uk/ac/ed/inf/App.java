package uk.ac.ed.inf;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Currently used for testing please ignore
 */
public class App {


    public static void main(String[] args) throws IOException, InvalidPizzaCombinationException, ParseException {
        String dateString = "";
        Date date = null;
        if (args.length == 1) {
            dateString = args[0];
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                date = dateFormat.parse(args[0]);
            } catch (DateTimeParseException e) {
                System.err.println("Invalid input: date entered must be valid and formatted as: YYYY-MM-DD");
                System.exit(1);
            }
        } else {
            System.err.println("Invalid input: please enter a date for orders in YYYY-MM-DD format");
            System.exit(1);
        }



        String defaultEndpoint = "https://ilp-rest.azurewebsites.net";
        CentralArea centralArea = CentralArea.getCentralAreaInstance(defaultEndpoint);
        centralArea.retrieveCentralAreaCoords();
        Restaurant[] restaurants = Restaurant.getRestaurantsFromRestServer(defaultEndpoint);
        NoFlyZones noFlyZones = NoFlyZones.getNoFlyZonesInstance(defaultEndpoint);
        Area[] zones = noFlyZones.getNoFlyZoneCoordinates();

        Orders orders = new Orders(defaultEndpoint);
        List<Order> ordersList = orders.getOrdersDay(dateString, date);
        Map map = new Map(restaurants, zones,  centralArea);
        Clock clock = Clock.systemDefaultZone();
        Integer start = Math.round(clock.millis());
        Drone drone = new Drone(map, ordersList, restaurants);
        do {
            drone.move();
        }
        while (!drone.flightFinished && drone.getMovesRemaining() > 0);


        var fp = drone.getFlightPath();
        var mo = drone.getMoveOrderNos();
        var ticks = drone.getTicks();
        var dir = drone.getDirections();
        var droneMap = map.droneMap(fp);

        WriteDeliveries.writeFile(dateString, ordersList);
        WriteFlightpath.writeFile(dateString, mo, fp, dir, ticks, start);
        WriteDrone.writeDrone(dateString, droneMap);
        Map.writeMap();






    }


}
