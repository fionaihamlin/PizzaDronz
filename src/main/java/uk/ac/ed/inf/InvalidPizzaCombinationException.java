package uk.ac.ed.inf;

/**
 * Exception to be thrown by {@link Order} if the pizzas in an order are an invalid combination
 *
 * @author fionahamlin
 */
public class InvalidPizzaCombinationException extends Exception {
    /**
     * Throws exception for invalid combination of menu items informing the user of the error.
     *
     * @param message To inform customer their order is not a valid combination as a String.
     */
    public InvalidPizzaCombinationException(String message) {
        super(message);
    }
}
