import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class KdTree {

    private Node root = null;
    private int size;
    private Node points;
    private final int XMIN = 0;
    private final int XMAX = 1;
    private final int YMIN = 0;
    private final int YMAX = 1;

    private class Node {
        // the point
        private Point2D p;

        // the axis-aligned rectangle corresponding to this node
        private RectHV rect;

        // the left/bottom subtree
        private Node lb;

        // the right/top subtree
        private Node rt;
    }

    // construct an empty set of points
    public KdTree() {
        this.points = new Node();
        this.size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return (this.size == 0);

    }

    // number of points in the set
    public int size() {
        return (this.size);
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }

        this.root = insert(this.root, p, true);

    }

    private Node insertNewNode(Node root, Point2D p) {
        Node newNode = new Node();
        newNode.lb = null;
        newNode.rt = null;
        newNode.rect = new RectHV(XMIN, YMIN, XMAX, YMAX);
        newNode.p = p;
        this.size++;
        return newNode;
    }

    private Node insert(Node root, Point2D p, boolean evenLevel) {
        // if the node is empty
        if (root == null) {
            return (this.insertNewNode(root, p));
        }

        // Is thie even level? 0, 2, 4, ...?
        if (evenLevel) {
            if (root.p.compareTo(p) == 0) {
                return root;
            }
            if (p.x() < root.p.x()) {
                root.lb = insert(root.lb, p, !evenLevel);
            } else {
                root.rt = insert(root.rt, p, !evenLevel);
            }
        }

        if (!evenLevel) {
            if (root.p.compareTo(p) == 0) {
                return root;
            }
            if (p.y() < root.p.y()) {
                root.lb = insert(root.lb, p, !evenLevel);
            } else {
                root.rt = insert(root.rt, p, !evenLevel);
            }
        }

        return root;

    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }
        return (false);
    }

    // draw all points to standard draw
    public void draw() {

    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("A rectangle needs to be supplied as an argument");
        }
        SET<Point2D> s = new SET<Point2D>();

        return (s);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    // Cost: Linear in the size of this.points
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }
        Point2D nearestNeighbor = null;

        return (nearestNeighbor);
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {

    }
}
