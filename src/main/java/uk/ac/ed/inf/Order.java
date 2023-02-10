package uk.ac.ed.inf;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing an order placed through the PizzaDronz service. Stores all necessary order information
 * and can calculate total cost of order using the information from the @class Restaurant.
 *
 * @author fionahamlin
 */
public class Order {
    private static final Integer DELIVERY_FEE = 100;
    private final String orderNo;
    private final String orderDate;
    private final String customer;
    private final String creditCardNumber;
    private final String creditCardExpiry;
    private final String cvv;
    private final String[] orderItems;
    private final Integer priceTotalInPence;
    private Restaurant orderRestaurant = null;
    private float orderTicks;
    private Integer deliveryCost = 0;
    private OrderOutcome orderOutcome = null;


    /**
     * Order object containing makeup of an order.
     *
     * @param orderNo           Number assigned to order as an Integer
     * @param orderDate         Date order was made as a Date object
     * @param customer          Name of customer making order as a String
     * @param creditCardNumber  Number of credit card used order payment as a String
     * @param creditCardExpiry  Expiry date of credit card used for order payment as a Date object
     * @param cvv               cvv of credit card used for order payment as an Integer
     * @param priceTotalInPence Total price of order in pence as an Integer
     * @param orderItems        List of pizzas ordered as a String array
     */
    public Order(@JsonProperty("orderNo") String orderNo, @JsonProperty("orderDate") String orderDate, @JsonProperty("customer") String customer, @JsonProperty("creditCardNumber") String creditCardNumber, @JsonProperty("creditCardExpiry") String creditCardExpiry, @JsonProperty("cvv") String cvv, @JsonProperty("priceTotalInPence") Integer priceTotalInPence, @JsonProperty("orderItems") String[] orderItems) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.customer = customer;
        this.creditCardNumber = creditCardNumber;
        this.creditCardExpiry = creditCardExpiry;
        this.cvv = cvv;
        this.priceTotalInPence = priceTotalInPence;
        this.orderItems = orderItems;


    }

    /**
     * Gets the pizzas ordered in current order.
     *
     * @return List of pizzas orders as String objects.
     */
    public List<String> getPizzasOrdered() {
        return Arrays.asList(this.orderItems);
    }


    /**
     * Method calculates the total cost to deliver a valid pizza order in pence from participating restaurants.
     * It will also identify if list of pizzas in order is not a valid order and throw and error.
     *
     * @param restaurants   List of @class Restaurant objects and their menus representing participating restaurants.
     * @param pizzasOrdered Pizzas in order as String ArrayList
     * @return The total cost of order and delivery in pence.
     * @throws InvalidPizzaCombinationException If pizza order is invalid due to number of pizzas or due to being
     *                                          ordered from different restaurants.
     */


    public int getDeliveryCost(Restaurant[] restaurants, List<String> pizzasOrdered) throws InvalidPizzaCombinationException {
        /**
         * Preset base delivery fee
         */
        int deliveryFee = 100;
        int totalCost = 0;

        ArrayList<String> pizzasLeft = new ArrayList<>(List.copyOf(pizzasOrdered));

        /**
         * Add price of ordered pizzas to total cost.
         */
        if (pizzasOrdered.size() > 0 && pizzasOrdered.size() < 5) {
            for (Restaurant restaurant : restaurants) {
                Menu[] menu = restaurant.getMenu();
                for (Menu restaurantMenu : menu) {
                    for (String pizza : pizzasOrdered) {
                        if (restaurantMenu.item().equals(pizza)) {
                            totalCost += restaurantMenu.price();
                            pizzasLeft.remove(pizza);
                            if (this.orderRestaurant == null) {
                                this.orderRestaurant = restaurant;
                            }
                        }
                    }
                    if (pizzasLeft.isEmpty()) {
                        break;
                    }
                }

                /**
                 * Throw @InvalidPizzaCombinationException if pizzas are ordered from multiple restaurants or are not
                 * on the menu of any participating restaurant.
                 */
                if (!pizzasLeft.isEmpty() && pizzasLeft.size() < pizzasOrdered.size()) {
                    this.deliveryCost = 0;
                    this.orderOutcome = OrderOutcome.InvalidPizzaCombinationMultipleSuppliers;
                    throw new InvalidPizzaCombinationException("All pizzas must be ordered from the same restaurant");
                }
            }

        }
        /**
         * Throw @InvalidPizzaCombinationException of the number of pizzas is not between one and four.
         */
        else {
            this.orderOutcome = OrderOutcome.InvalidPizzaCount;
            this.deliveryCost = 0;
            throw new InvalidPizzaCombinationException("You can order between 1 and 4 pizzas");
        }
        this.deliveryCost = (totalCost + deliveryFee);
        return totalCost + deliveryFee;
    }


    @Override
    public String toString() {
        return "Order{" + "orderNo=" + orderNo + ", orderDate=" + orderDate + ", customer='" + customer + '\'' + ", creditCardNumber='" + creditCardNumber + '\'' + ", creditCardExpiry=" + creditCardExpiry + ", cvv=" + cvv + ", priceTotalInPence=" + priceTotalInPence + ", orderItems=" + Arrays.toString(orderItems) + '}';
    }

    /**
     * Gets the month of the order for credit card validation.
     *
     * @return Month of order as Integer.
     */
    private Integer getOrderMonth() {
        return Integer.parseInt(this.orderDate.substring(5, 7));
    }

    /**
     * Gets year of the order for credit card validation.
     *
     * @return Last two digits of year as Integer.
     */
    private Integer getOrderYear() {
        return Integer.parseInt(this.orderDate.substring(2, 4));
    }


    /**
     * Checks the expiry date of order. Checks that expiry date is in valid form and that date of card expiry is not before
     * order date.
     *
     * @return True if expiry date is valid, false otherwise.
     */
    public boolean checkExpiry() {
        Integer month;
        Integer year;
        Integer orderYear = getOrderYear();
        Integer orderMonth = getOrderMonth();
        boolean valid = false;
        if (this.creditCardExpiry.contains("/")) {
            String[] monYr = this.creditCardExpiry.split("/");
            if (monYr.length == 2) {
                String mon = monYr[0];
                String yr = monYr[1];
                if (mon.length() == 2 && yr.length() == 2) {
                    month = Integer.parseInt(mon);
                    year = Integer.parseInt(yr);
                    if ((year == orderYear && month >= orderMonth) || (year > orderYear && month <= 12)) {
                        valid = true;
                    }
                }
            }
        }

        if (!valid) {
            this.orderOutcome = OrderOutcome.InvalidExpiryDate;
        }
        return valid;

    }


    /**
     * Checks that the cvv is valid, meaning it only has three digits all of which are between 0 and 9.
     *
     * @return True of cvv is valid, false otherwise.
     */
    public boolean checkCvv() {
        if (this.cvv.length() != 3 || !allDigits(this.cvv)) {
            this.orderOutcome = OrderOutcome.InvalidCvv;
            return false;
        }
        return true;
    }

    /**
     * Checks that all characters in a String are digits between 0 and 9 for use in credit card validation.
     *
     * @param numToCheck The number to check as a String.
     * @return True if all characters are digits, false otherwise.
     */
    public boolean allDigits(String numToCheck) {
        List<Character> digits = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        for (int i = 0; i < numToCheck.length(); i++) {
            if (!digits.contains(numToCheck.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks that a credit card number is valid by checking its length prefix as well as doing Luhn verification.
     *
     * @return True if card number is valid, false otherwise.
     */
    public boolean checkCardNumber() {
        if (creditCardNumber == null || creditCardNumber.length() != 16 || !allDigits(creditCardNumber)) {
            orderOutcome = OrderOutcome.InvalidCardNumber;

            return false;
        } else if (!isMasterCardOrVisa(Character.getNumericValue(creditCardNumber.charAt(0)), Integer.parseInt(creditCardNumber.substring(0, 2)), Integer.parseInt(creditCardNumber.substring(0, 4)))) {
            orderOutcome = OrderOutcome.InvalidCardNumber;
            return false;
        } else if (!checkCardByLuhn()) {
            orderOutcome = OrderOutcome.InvalidCardNumber;
            return false;
        }
        return true;

    }

    /**
     * Checks that a credit card number is a MasterCard or Visa by checking one of its prefixes fall within  a standard range.
     *
     * @param first     First digit of card for verification
     * @param firstTwo  First two digits of card for verification
     * @param firstFour First four digits of card for verification
     * @return
     */
    private boolean isMasterCardOrVisa(Integer first, Integer firstTwo, Integer firstFour) {
        return (firstTwo >= 51 && firstTwo <= 55) || (firstFour >= 2221 && firstFour <= 2720) || (first == 4);
    }


    /**
     * Checks if credit card is valid via the luhn algorithm
     * Adapted from: https://www.geeksforgeeks.org/luhn-algorithm/
     *
     * @return True if algorithm finds number valid
     */
    private boolean checkCardByLuhn() {
        int nSum = 0;
        boolean second = false;
        for (int i = 15; i >= 0; i--) {
            int d = this.creditCardNumber.charAt(i);
            if (second) {
                d = d * 2;
                nSum += d / 10;
                nSum += d % 10;
                second = !second;
            }
        }
        return ((nSum & 10) == 0);
    }


    public OrderOutcome getOrderOutcome() {
        return orderOutcome;
    }

    public void setOrderOutcome(OrderOutcome outcome) {
        orderOutcome = outcome;
    }

    public Restaurant getOrderRestaurant() {
        return this.orderRestaurant;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public Integer deliveryCostInPence() {
        return this.deliveryCost;
    }

    public Integer getPriceTotalInPence() {
        return this.priceTotalInPence;
    }


}
