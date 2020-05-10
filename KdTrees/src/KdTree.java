import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private Node root = null;
    private int size;

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

        this.root = insert(this.root, p, true, new RectHV(XMIN, YMIN, XMAX, YMAX));

    }

    private Node insertNewNode(Node root, Point2D p, RectHV rectangle) {
        Node newNode = new Node();
        newNode.lb = null;
        newNode.rt = null;
        newNode.rect = rectangle;
        newNode.p = p;
        this.size++;
        return newNode;
    }

    private RectHV getBottomSplitOfParentRectangle(RectHV parentRectange, Point2D parentPoint) {
        // split parentRectangle into top and bottom parts.
        // return the bottom part. Boundary determined by the y-coordinate of the
        // parent.
        return (new RectHV(parentRectange.xmin(), parentRectange.ymin(), parentRectange.xmax(), parentPoint.y()));
    }

    private RectHV getTopSplitOfParentRectangle(RectHV parentRectange, Point2D parentPoint) {
        // split parentRectangle into top and bottom parts.
        // return the top part. Boundary determined by the y-coordinate of the parent.
        return (new RectHV(parentRectange.xmin(), parentPoint.y(), parentRectange.xmax(), parentRectange.ymax()));
    }

    private RectHV getLeftSplitOfParentRectangle(RectHV parentRectange, Point2D parentPoint) {
        // split parentRectangle into left and right parts. Right boundary determined by
        // the x-coordinate of the parent.

        return (new RectHV(parentRectange.xmin(), parentRectange.ymin(), parentPoint.x(), parentRectange.ymax()));
    }

    private RectHV getRightSplitOfParentRectangle(RectHV parentRectange, Point2D parentPoint) {
        // split parentRectangle into left and right parts. Left boundary determined by
        // the x-coordinate of the parent.
        return (new RectHV(parentPoint.x(), parentRectange.ymin(), parentRectange.xmax(), parentRectange.ymax()));
    }

    private Node insert(Node root, Point2D p, boolean evenLevel, RectHV rectangle) {
        // if the node is empty
        if (root == null) {
            return (this.insertNewNode(root, p, rectangle));
        }

        // Is thie even level? 0, 2, 4, ...?
        if (evenLevel) {
            if (root.p.compareTo(p) == 0) {
                return root;
            }

            // parent rectangle is split top/bottom
            if (p.x() < root.p.x()) {
                root.lb = insert(root.lb, p, !evenLevel, getLeftSplitOfParentRectangle(root.rect, root.p));
            } else {
                root.rt = insert(root.rt, p, !evenLevel, getRightSplitOfParentRectangle(root.rect, root.p));
            }
        }

        if (!evenLevel) {
            if (root.p.compareTo(p) == 0) {
                return root;
            }
            // parent rectangle is split left/right
            if (p.y() < root.p.y()) {
                root.lb = insert(root.lb, p, evenLevel, getBottomSplitOfParentRectangle(root.rect, root.p));
            } else {
                root.rt = insert(root.rt, p, evenLevel, getTopSplitOfParentRectangle(root.rect, root.p));
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
        this.draw(this.root, true);
    }

    private void draw(Node node, boolean evenLevel) {
        if (node == null) {
            return;
        }
        if (evenLevel) {
            // red vertical line
            Point2D lowerPoint = new Point2D(node.p.x(), node.rect.ymin());
            Point2D upperPoint = new Point2D(node.p.x(), node.rect.ymax());
            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.RED);
            lowerPoint.drawTo(upperPoint);
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.BLACK);
            node.p.draw();
        } else {
            // blue horizontal line
            Point2D leftPoint = new Point2D(node.rect.xmin(), node.p.y());
            Point2D rightPoint = new Point2D(node.rect.xmax(), node.p.y());
            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.BLUE);
            leftPoint.drawTo(rightPoint);
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.BLACK);
            node.p.draw();
        }

        this.draw(node.lb, !evenLevel);
        this.draw(node.rt, !evenLevel);

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
