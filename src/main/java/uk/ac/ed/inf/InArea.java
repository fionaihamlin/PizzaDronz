package uk.ac.ed.inf;


import java.util.List;

/**
 * Class checks if point is in or on perimeter of a polygon.
 * This class is adapted from an algorithm and implementation posted on geeksforgeeks.org contributed by @Aarti_Rathi
 * {@link <a href="https://www.geeksforgeeks.org/how-to-check-if-a-given-point-lies-inside-a-polygon/"></a>}
 */

public class InArea {


    /**
     * Determines if a point in or on a given polygon.
     *
     * @param coordinates List of LngLat objects representing vertices of polygon as longitude and latitude coordinates
     * @param n           the number of vertices of the polygon as an int
     * @param point       LngLat object representing the coordinates of point that will be checked
     * @return True if point is in or on the perimeter of the polygon and false otherwise
     */
    public boolean inArea(List<LngLat> coordinates, Integer n, LngLat point) {
        if (n < 3) {
            return false;
        }
        Double inf = 100000.0;
        LngLat infCoord = new LngLat(inf, point.lat());
        Integer reduceCount = 0;
        Integer count = 0;
        Integer i = 0;
        do {
            Integer next = (i + 1) % n;
            if (coordinates.get(i).lng() == point.lng()) reduceCount++;
            if (isIntersect(coordinates.get(i), coordinates.get(next), point, infCoord)) {
                if (direction(coordinates.get(i), point, coordinates.get(next)) == 0) {
                    return onLine(coordinates.get(i), coordinates.get(next), point);
                }
                count++;
            }
            i = next;
        } while (i != 0);
        count -= reduceCount;


        return (count % 2 != 0);


    }

    /**
     * Determines if point is on a line by checking that its coordinates bounded between the coordinates of the
     * endpoints of the line.
     *
     * @param p1      Coordinates for one endpoint of the line as LngLat object
     * @param p2      Coordinates for other endpoint of the line as LngLat object
     * @param myPoint Coordinates for the point that is being tested as LngLat object
     * @return True if point is on the line and false otherwise
     */
    private boolean onLine(LngLat p1, LngLat p2, LngLat myPoint) {
        return myPoint.lat() <= Math.max(p1.lat(), p2.lat()) && myPoint.lat() >= Math.min(p1.lat(), p2.lat())
                && myPoint.lng() <= Math.max(p1.lng(), p2.lng()) && myPoint.lng() >= Math.min(p1.lng(), p2.lng());
    }

    /**
     * Determines the direction vector of three points.
     *
     * @param p1 Coordinates of first point as LngLat object
     * @param p2 Coordinates of second point as LngLat object
     * @param p3 Coordinates of third point as LngLat object
     * @return 0 if all three points fall on same line, 1 the direction is clockwise, and 2 if it is counterclockwise
     */
    private Integer direction(LngLat p1, LngLat p2, LngLat p3) {
        Double val = (p2.lat() - p1.lat()) * (p3.lng() - p2.lng()) - (p2.lng() - p1.lng()) * (p3.lat() - p2.lat());

        if (val == 0) {

            return 0;
        } else if (val < 0) {

            return 2;
        }

        return 1;
    }

    /**
     * Checks if two lines intersect at any point by checking for obvious intersections and seeing if any point
     * falls within bounds of collinear points.
     *
     * @param l11 Coordinates for first endpoint for line 1 as LngLat object
     * @param l12 Coordinates for second endpoint for line 1 as LngLat object
     * @param l21 Coordinates for first endpoint for line 2 as LngLat object
     * @param l22 Coordinates for second endpoint for line 2 as LngLat object
     * @return True if the two lines intersect anywhere and false if they do not.
     */
    private boolean isIntersect(LngLat l11, LngLat l12, LngLat l21, LngLat l22) {
        Integer d1 = direction(l11, l12, l21);
        Integer d2 = direction(l11, l12, l22);
        Integer d3 = direction(l21, l22, l11);
        Integer d4 = direction(l21, l22, l12);

        if (d1 != d2 && d3 != d4) {
            return true;
        }

        if (d1 == 0 && onLine(l11, l12, l21)) {
            return true;
        }

        if (d2 == 0 && onLine(l11, l12, l22)) {
            return true;
        }
        if (d3 == 0 && onLine(l21, l22, l11)) {
            return true;
        }
        return d4 == 0 & onLine(l21, l22, l12);
    }
}
