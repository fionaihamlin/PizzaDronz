package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing the pizza delivery drone.
 *
 * @author s2107575
 */
public class Drone {
    private final LngLat APPLETON_COORDS = new LngLat(-3.186874, 55.944494);
    public boolean flightFinished;
    private Integer movesRemaining;
    private LngLat currentLocation;

    private Map map;
    private List<Order> ordersCompleted;

    private List<Order> allOrders;
    private List<Order> validOrders;
    private List<Point> flightPath;

    private List<Order> orderRoute;
    private List<Integer> ticks;
    private Restaurant[] restaurants;


    private List<String> directions;
    private List<String> moveOrderNos;


    private int orderCounter;
    private DronePos dronePos;
    private boolean finalReturn;

    private LngLat nextPos;


    /**
     * Drone object that drives its movements and all its movement data.
     *
     * @param map    Map representing the PizzaDronz area of service and its elements.
     * @param orders Orders for given day to find route for.
     */
    public Drone(Map map, List<Order> orders, Restaurant[] restaurant) {

        this.map = map;
        this.currentLocation = APPLETON_COORDS;
        this.dronePos = new DronePos(this.currentLocation);
        this.flightPath = new ArrayList<>();
        this.flightPath.add(this.currentLocation.asPoint());
        this.flightFinished = false;


        this.directions = new ArrayList<>();
        this.directions.add(dronePos.getDirection().getAngle().toString());


        this.restaurants = restaurant;
        this.allOrders = orders;
        this.validOrders = allValidOrders();
        this.ordersCompleted = new ArrayList<>();


        this.orderRoute = getRoute();
        this.ticks = new ArrayList<>();
        this.moveOrderNos = new ArrayList<>();
        this.finalReturn = false;


        this.orderCounter = 0;
        this.nextPos = orderRoute.get(0).getOrderRestaurant().getLngLat();
        this.moveOrderNos.add(orderRoute.get(0).getOrderNo());
        this.ticks.add(Math.round(Clock.systemDefaultZone().millis()));


        this.movesRemaining = 2000;


    }

    /**
     * Gets flightpath of the drone.
     *
     * @return The coordinates of the drone's flightpath as list Point objects.
     */
    public List<Point> getFlightPath() {
        return this.flightPath;
    }

    /**
     * Gets the time that each move occurred.
     *
     * @return The times each move occurred as list of Integers representing the time in milliseconds.
     */
    public List<Integer> getTicks() {
        return this.ticks;
    }

    /**
     * Gets order numbers of the order the drone was delivering at the time of each move.
     *
     * @return The order numbers as a list of String objects.
     */
    public List<String> getMoveOrderNos() {
        return this.moveOrderNos;
    }

    /**
     * Gets the directions, or angles, of each move made by the drone.
     *
     * @return The angle measurement of each move as a list of String objects.
     */
    public List<String> getDirections() {
        return this.directions;
    }

    /**
     * Gets the number of moves the drone has remaining from its battery constraints.
     *
     * @return Number of moves remaining as an Integer object.
     */
    public Integer getMovesRemaining() {
        return movesRemaining;
    }

    /**
     * Moves the drone using greedy algorithm according to ordering of orders to complete and return to Appleton Tower,
     * accounting for movement constraints and battery life of drone.
     * Adds angle direction and coordinates of each move, the time the move occurred, the order number for each move
     * to class variables.
     */
    public void move() {

        LngLat current = this.currentLocation;
        LngLat target = this.nextPos;


        if (this.ordersCompleted.size() == this.validOrders.size() && current.closeTo(APPLETON_COORDS)) {
            this.flightFinished = true;

        } else {
            Order targetOrder = this.orderRoute.get(this.orderCounter);
            if (!this.finalReturn) {
                this.moveOrderNos.add(targetOrder.getOrderNo());
            } else {
                this.moveOrderNos.add("no_order");
            }

            var nextPos = this.dronePos.findMove(map, target);
            current = nextPos;
            this.currentLocation = current;
            this.ticks.add(Math.round(Clock.systemDefaultZone().millis()));
            this.flightPath.add(current.asPoint());
            this.directions.add(this.dronePos.getDirection().getAngle().toString());

            this.movesRemaining--;

            if (current.closeTo(target)) {
                this.flightPath.add(current.asPoint());
                this.directions.add("null");
                this.ticks.add(Math.round(Clock.systemDefaultZone().millis()));
                this.moveOrderNos.add(targetOrder.getOrderNo());

                this.movesRemaining--;
                if (!dronePos.isOnReturn()) {
                    this.ordersCompleted.add(targetOrder);
                    targetOrder.setOrderOutcome(OrderOutcome.Delivered);
                    this.dronePos.setOnReturn(true);
                    if (this.movesRemaining < 150) {
                        this.finalReturn = true;
                    } else {
                        this.nextPos = APPLETON_COORDS;
                    }

                } else {
                    this.dronePos.setOnReturn(false);
                    if (finalReturn) {
                        this.flightFinished = true;
                    } else {
                        orderCounter++;
                        this.nextPos = orderRoute.get(this.orderCounter).getOrderRestaurant().getLngLat();
                    }
                }

            }

        }
    }

    /**
     * Gets all orders for given day and returns the ones that are valid.
     *
     * @return List of Order objects that are valid orders.
     */
    private List<Order> allValidOrders() {
        List<Order> validOrders = new ArrayList<>();

        for (Order order : this.allOrders) {

            List<String> orderedT = order.getPizzasOrdered();

            if (order.checkCardNumber() && order.checkCvv() && order.checkExpiry()) {
                try {
                    order.getDeliveryCost(this.restaurants, orderedT);

                } catch (InvalidPizzaCombinationException ignored) {

                }


                if (!order.getPriceTotalInPence().equals(order.deliveryCostInPence())) {
                    order.setOrderOutcome(OrderOutcome.InvalidTotal);
                }

                if (order.getOrderOutcome() == null) {
                    validOrders.add(order);
                    order.setOrderOutcome(OrderOutcome.ValidButNotDelivered);
                }


            }
        }


        return validOrders;
    }

    /**
     * Computes route of orders to complete for the drone based on their distance to Appleton Tower.
     *
     * @return List of Order objects ordered for the drone.
     */
    public List<Order> getRoute() {
        List<Order> validCopy = new ArrayList<>(List.copyOf(validOrders));
        List<Restaurant> rankedByDist = this.getRestByDist();
        List<Order> route = new ArrayList<>();
        for (int i = 0; i < rankedByDist.size(); i++) {
            Restaurant currentRestaurant = rankedByDist.get(i);
            int j = 0;

            while (!validCopy.isEmpty() && j < validOrders.size()) {
                Order currentOrder = validOrders.get(j);
                if (currentOrder.getOrderRestaurant().equals(currentRestaurant)) {
                    route.add(currentOrder);
                    validCopy.remove(currentOrder);
                }
                j++;
            }
        }
        return route;
    }

    /**
     * Sorts restaurants by there distance to Appleton Tower
     *
     * @return List of restaurants sorted by distance
     */
    public List<Restaurant> getRestByDist() {

        List<Restaurant> restaurantList = Arrays.asList(restaurants);
        List<Restaurant> sortedRestaurants = restaurantList.stream().sorted(Comparator.comparing(Restaurant::getDistToAppleton)).collect(Collectors.toList());

        return sortedRestaurants;
    }


}
