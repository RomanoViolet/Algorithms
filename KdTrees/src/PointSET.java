import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

// https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/SET.html
import edu.princeton.cs.algs4.SET;

public class PointSET {

    private SET<Point2D> points;

    // construct an empty set of points
    public PointSET() {

        this.points = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return (this.points.isEmpty());

    }

    // number of points in the set
    public int size() {
        return (this.points.size());
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }
        if (!this.points.contains(p)) {
            this.points.add(p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }
        return (this.points.contains(p));
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D currentPoint : points) {
            currentPoint.draw();
        }

    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("A rectangle needs to be supplied as an argument");
        }
        SET<Point2D> s = new SET<Point2D>();
        for (Point2D p : this.points) {
            if (rect.contains(p)) {
                s.add(p);
            }
        }
        return (s);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    // Cost: Linear in the size of this.points
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }
        Point2D nearestNeighbor = null;
        for (Point2D thisPoint : this.points) {
            // distanceTo uses square-roots which is expensive.
            if ((nearestNeighbor == null) || (nearestNeighbor.distanceSquaredTo(p) > thisPoint.distanceSquaredTo(p))) {
                nearestNeighbor = thisPoint;
            }
        }
        return (nearestNeighbor);
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {

    }
}
