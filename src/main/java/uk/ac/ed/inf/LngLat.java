package uk.ac.ed.inf;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.util.List;


/**
 * Record stores latitude and longitude coordinates in the form of an Object.
 * It can determine that a point is or is not in the Central Area, the distance from one point to another,
 * and determine if two points are relatively close together.
 * Can determine the next position for the drone from LngLat coordinate and {@link Direction}
 *
 * @param lng longitude coordinate as double
 * @param lat latitude coordinate as double
 * @author S2107575
 */
public record LngLat(
        @JsonIgnoreProperties("name")
        @JsonProperty("longitude") Double lng,
        @JsonProperty("latitude") Double lat) {

    public static final Double TOLERANCE = 0.00015;
    public static final Double MOVE_DIST = 0.00015;


    /**
     * Determines if LatLng coordinate is within the University's Central Area by retrieving the Central Area coordinates
     * from {@link CentralArea} and checking with {@link InArea}.
     *
     * @return True if coordinate is in the central area and false otherwise
     * @throws IOException If URL cannot be reached in {@link CentralArea}
     */
    public boolean inCentralArea() {
        CentralArea centralArea = CentralArea.getCentralAreaInstance("");
        List<LngLat> coords = centralArea.getCoords();
        System.out.println(coords);
        InArea inArea = new InArea();
        return inArea.inArea(coords, coords.size(), this);
    }


    /**
     * Computes Euclidean distance between LatLng coordinate and another one.
     *
     * @param point LatLang coordinate to compute distance to
     * @return Euclidean distance between the two coordinates as a double
     */
    public Double distanceTo(LngLat point) {
        Double lngDist = Math.pow(this.lng - point.lng(), 2);
        Double latDist = Math.pow(this.lat - point.lat(), 2);
        return Math.sqrt(lngDist + latDist);
    }

    /**
     * Checks if LatLng coordinate is close to another by seeing if its within the distance tolerance of another.
     *
     * @param point Coordinate to the check distance
     * @return True if distance is within tolerance and false otherwise.
     */
    public boolean closeTo(LngLat point) {
        return this.distanceTo(point) <= LngLat.TOLERANCE;
    }

    /**
     * Computes a new LatLng coordinate representing the position of the drone after a move in a specified compass direction.
     *
     * @param direction Compass direction as Enum type {@link Direction}
     * @return the coordinate representing the next position
     */
    public LngLat nextPosition(Direction direction) {
        if (direction == null) {
            return this;
        } else {
            Double angle = Math.toRadians(direction.getAngle());
            Double newLat = this.lat + (Math.sin(angle) * LngLat.MOVE_DIST);
            Double newLng = this.lng + (Math.cos(angle) * LngLat.MOVE_DIST);
            return new LngLat(newLng, newLat);
        }
    }

    /**
     * Gives LngLat object as GeoJson point.
     *
     * @return GeoJson point representing LngLat object.
     */
    public Point asPoint() {
        return Point.fromLngLat(this.lng, this.lat);
    }


}



