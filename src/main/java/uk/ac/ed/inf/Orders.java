package uk.ac.ed.inf;


import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class representing collections of orders.
 *
 * @author s2107575
 */
public class Orders {
    private static final String DEFAULT_ENDPOINT = "https://ilp-rest.azurewebsites.net";
    private static URL baseUrl;
    private static List<Order> allOrders;


    /**
     * Constructor to set up class to deal with collections of orders.
     *
     * @param base Base endpoint for rest server.
     */
    public Orders(String base) {
        try {
            this.baseUrl = new URL(base);
        } catch (MalformedURLException ignore) {
            try {
                this.baseUrl = new URL(DEFAULT_ENDPOINT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Retrieves all orders from REST server
     */
    private void retrieveAllOrders() {
        Order[] all = new RestClient(this.baseUrl).deserialize("/orders", Order[].class);
        this.allOrders = Arrays.asList(all);
    }

    /**
     * Returns all orders held by REST server.
     *
     * @return List of all orders in REST server as Order objects.
     */
    public List<Order> getAllOrders() {
        if (allOrders == null) {
            this.retrieveAllOrders();
        }
        return allOrders;
    }

    /**
     * Gets all orders for a given day from REST server. Checks that date given is in valid format and is serviced
     * by server.
     *
     * @param dateString String representing given.
     * @param myDate     Given date represented by Date object.
     * @return List of Order objects that were placed that day.
     * @throws ParseException If date cannot be parsed.
     */
    public List<Order> getOrdersDay(String dateString, Date myDate) throws ParseException {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);


        Date startRange = sdf.parse("2023-01-01");
        Date endRange = sdf.parse(("2023-05-31"));

        if (myDate.before(startRange) || myDate.after(endRange)) {
            throw new RuntimeException("Given date out of range please enter day between 1 January 2023 and " +
                    "31 May 2023");
        } else {
            String endpoint = "/orders/" + dateString;
            Order[] ordersFromDate = new RestClient(baseUrl).deserialize(endpoint, Order[].class);
            return Arrays.asList(ordersFromDate);
        }

    }


}