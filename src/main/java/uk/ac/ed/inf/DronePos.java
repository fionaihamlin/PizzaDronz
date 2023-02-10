package uk.ac.ed.inf;

import com.mapbox.geojson.Polygon;

import java.awt.geom.Line2D;

/**
 * Class representing the drones position and computes next moves.
 *
 * @author s2107575
 */
public class DronePos {
    private LngLat position;

    private Direction direction;

    private boolean onReturn;

    /**
     * DronePos class representing its position and movement components.
     *
     * @param position Sets the initial position of drone.
     */
    public DronePos(LngLat position) {
        this.position = position;
        this.direction = Direction.E;
        this.onReturn = false;
    }

    /**
     * Gets current position of drone.
     *
     * @return Coordinates of the drone's position as a LngLat object.
     */
    public LngLat getPosition() {
        return this.position;
    }

    /**
     * Gets current direction of drone.
     *
     * @return Direction as a Direction enum.
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * Checks if Drone is on a return from an order.
     *
     * @return True if drone is returning and false otherwise.
     */
    public boolean isOnReturn() {
        return onReturn;
    }

    /**
     * Allows for the setting of the return of the drone.
     *
     * @param onReturn Boolean representing if the drone is on a returning journey or not.
     */
    public void setOnReturn(boolean onReturn) {
        this.onReturn = onReturn;
    }

    /**
     * Simple Greedy method to find the best direction for the drone to fly based on distance of move to target.
     *
     * @param currentPos Current position of the drone as coordinates represented by LngLat object.
     * @param targetPos  Target position of the drone as coordinates represented by LngLat object.
     * @return Best direction for drone to move in.
     */
    public Direction getBestDirection(LngLat currentPos, LngLat targetPos) {
        Double minDistance = Double.MAX_VALUE;
        Direction bestDirection = Direction.E;
        for (Direction d : Direction.values()) {
            LngLat possibleMove = currentPos.nextPosition(d);
            Double distance = possibleMove.distanceTo(targetPos);
            if (distance < minDistance) {
                minDistance = distance;
                bestDirection = d;
            }

        }
        return bestDirection;
    }


    /**
     * Checks that a proposed move for the drone is valid by checking that it is not reentering the Central Area
     * before it has delivered an order and that it is not entering a NoFLyZone.
     *
     * @param map     Map representing the PizzaDronz area of service and its elements.
     * @param nextPos Proposed next position for the drone.
     * @return True if move does not violate any constraints and false otherwise.
     */
    private boolean validMove(Map map, LngLat nextPos) {

        boolean illegalLeavingCA = false;
        var pathToTry = new Line2D.Double(this.position.lat(), this.position.lng(),
                nextPos.lat(), nextPos.lng());
        if (this.position.inCentralArea() && !nextPos.inCentralArea() && this.onReturn) {
            illegalLeavingCA = true;
        }
        boolean illegalInNoFly = false;

        var noFly = map.getNoFlyZones();
        for (Polygon zone : noFly) {
            var points = zone.coordinates().get(0);
            for (int i = 0; i < points.size() - 1; i++) {
                int j = (i + 1) % points.size();
                Line2D zoneBarrier = new Line2D.Double(points.get(i).longitude(),
                        points.get(i).latitude(), points.get(j).longitude(), points.get(j).latitude());
                if (pathToTry.intersectsLine(zoneBarrier)) {
                    illegalInNoFly = true;
                    break;
                }

            }
            if (illegalInNoFly) {
                break;
            }


        }

        return !illegalLeavingCA && !illegalInNoFly;

    }

    /**
     * Method to find next move for the drone.
     *
     * @param map       Map representing the PizzaDronz area of service and its elements.
     * @param targetPos Position the drone is moving towards.
     * @return The coordinate of the next move for the drone as LngLat object.
     */
    public LngLat findMove(Map map, LngLat targetPos) {
        var bestDirection = getBestDirection(position, targetPos);
        var nextPos = position.nextPosition(bestDirection);

        if (validMove(map, nextPos)) {
            this.position = nextPos;
            this.direction = bestDirection;

        } else {
            double[] tryAngles = {-22.5, 22.5, -45, 45, -62.5, 62.5, -90, 90};
            for (int i = 0; i < tryAngles.length - 1; i++) {
                double tryAngle = bestDirection.getAngle() + tryAngles[i];
                Direction tryDirection = Direction.getDirection(tryAngle);
                var tryNextPos = position.nextPosition(tryDirection);
                if (validMove(map, tryNextPos)) {
                    this.position = tryNextPos;
                    this.direction = tryDirection;

                }
            }
        }
        return getPosition();

    }


}
