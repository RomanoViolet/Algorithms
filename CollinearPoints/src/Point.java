
// base: https://coursera.cs.princeton.edu/algs4/assignments/collinear/files/Point.java

/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;

public class Point implements Comparable<Point> {

    private final int x; // x-coordinate of this point
    private final int y; // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param x the <em>x</em>-coordinate of the point
     * @param y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point to standard
     * draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point. Formally, if
     * the two points are (x0, y0) and (x1, y1), then the slope is (y1 - y0) / (x1 -
     * x0). For completeness, the slope is defined to be +0.0 if the line segment
     * connecting the two points is horizontal; Double.POSITIVE_INFINITY if the line
     * segment is vertical; and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1)
     * are equal.
     *
     * @param that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        double slope;
        if ((this.x == that.x) && (this.y != that.y)) {
            // Double.POSITIVE_INFINITY if the line segment is vertical
            slope = Double.POSITIVE_INFINITY;
        } else if ((this.x == that.x) && ((this.y == that.y))) {
            // Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal
            slope = Double.NEGATIVE_INFINITY;
        } else {
            slope = (that.y - this.y) / (that.x - this.x);
        }

        return slope;

    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate. Formally,
     * the invoking point (x0, y0) is less than the argument point (x1, y1) if and
     * only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument point (x0
     *         = x1 and y0 = y1); a negative integer if this point is less than the
     *         argument point; and a positive integer if this point is greater than
     *         the argument point
     */
    public int compareTo(Point that) {
        if (this.y < that.y)
            return -1;
        if (this.y > that.y)
            return +1;
        if ((this.y == that.y) && (this.x < that.x))
            return -1;
        if ((this.y == that.y) && (this.x > that.x))
            return -1;
        return 0;
    }

    private class BySlopeComparator implements Comparator<Point> {
        // slope can be +∞, -∞, or a valid slope.
        // Java allows comparisons between +∞, -∞ like any other finite number.
        // @see https://stackoverflow.com/a/18885210
        @Override
        public int compare(Point a, Point b) {
            if (Point.this.slopeTo(a) < Point.this.slopeTo(b)) {
                return (-1);
            } else if (Point.this.slopeTo(a) > Point.this.slopeTo(b)) {
                return (1);
            } else {
                return (0);
            }
        }

    }

    /**
     * Compares two points by the slope they make with this point. The slope is
     * defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new BySlopeComparator();
    }

    /**
     * Returns a string representation of this point. This method is provide for
     * debugging; your program should not rely on the format of the string
     * representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {

        // Test Point comparison
        Point a = new Point(0, 0);
        Point b = new Point(0, 0);
        assert (a.slopeTo(b) == Double.NEGATIVE_INFINITY) : "Equality comparison is broken";

        Point c = new Point(0, 1);
        assert (a.slopeTo(c) == Double.POSITIVE_INFINITY) : "Equality comparison is broken";

        Point d = new Point(1, 1);
        assert (a.slopeTo(d) == 1) : "Slope comparison is incorrect";

        assert (a.compareTo(b) == 0) : "Comparison between equal points is broken ";
        assert (a.compareTo(c) == -1) : "Check that (0,0) < (0,1) failed.";

        Point e = new Point(1, 0);
        assert (a.compareTo(e) == -1) : "Check that (0,0) < (1,0) failed.";

        assert (a.slopeOrder().compare(c, d) == 1) : "Comparator is incorrect";

    }
}
