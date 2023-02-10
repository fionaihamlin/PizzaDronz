package uk.ac.ed.inf;

/**
 * Documents the different possible outcomes of an order.
 */
public enum OrderOutcome {
    /**
     * Order was delivered
     */
    Delivered,
    ValidButNotDelivered,

    /**
     * Card number for payment of order invalid
     */
    InvalidCardNumber,
    /**
     * Card expiry date for payment of order invalid
     */
    InvalidExpiryDate,
    /**
     * Card Cvv for payment of order invalid
     */
    InvalidCvv,
    /**
     * The total cost of the order is invalid
     */
    InvalidTotal,
    /**
     * Pizza in order undefined
     */
    InvalidPizzaNotDefined,
    /**
     * Number of pizzas in order invalid
     */
    InvalidPizzaCount,
    /**
     * All pizzas must be ordered from the same supplier
     */
    InvalidPizzaCombinationMultipleSuppliers,
    /**
     * Order invalid
     */
    Invalid
}
