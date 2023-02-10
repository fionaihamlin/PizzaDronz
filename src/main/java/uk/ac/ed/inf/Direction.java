package uk.ac.ed.inf;

/**
 * Documents the 16 major compass directions and their associated angles.
 */
public enum Direction {
    E(0.0),
    ENE(22.5),
    NE(45.0),
    NNE(67.5),
    N(90.0),
    NNW(112.5),
    NW(135.0),
    WNW(157.5),
    W(180.0),
    WSW(202.5),
    SW(225.0),
    SSW(247.5),
    S(270.0),
    SSE(292.5),
    SE(315.0),
    ESE(337.5);


    private final Double angle;


    Direction(Double angle) {
        this.angle = angle;
    }

    /**
     * Method that normalizes given angle and returns its equivalent Direction representing compass direction.
     *
     * @param myAngle to normalize and find Direction equivalence for.
     * @return Direction enum that is equivalent to given angle.
     */
    public static Direction getDirection(Double myAngle) {
        Direction myDirection = null;
        if (myAngle < 0) {
            myAngle += 360;
        } else if (myAngle > 360) {
            myAngle -= 360;
        }
        for (Direction d : Direction.values()) {
            if (d.getAngle() == myAngle) {
                myDirection = d;
            }
        }
        return myDirection;
    }

    /**
     * Retrieves angle of compass direction.
     *
     * @return Angle of compass direction in degrees.
     */
    public Double getAngle() {
        return this.angle;
    }


}
