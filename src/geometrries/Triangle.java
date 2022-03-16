package geometrries;

import primitives.Point;
import primitives.Vector;

public class Triangle extends Polygon {


    /**
     * Constructor
     * @param p1 type point
     * @param p2 type point
     * @param p3 type point
     */

    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }


    /**
     * Print Triangle's vertices and plane
     */
    @Override
    public String toString() {
        return "Triangle{" +
               "vertices=" + vertices +
               ", plane=" + _plane +
               '}';
    }

    /**
     * Get Normal
     * @return normal vector from the point to the Triangle
     */
    @Override
    public Vector getNormal(Point point) {
        return super.getNormal(point);
    }
}
