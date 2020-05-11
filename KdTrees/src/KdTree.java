import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Queue;

public class KdTree {

    private static final int XMIN = 0;
    private static final int XMAX = 1;
    private static final int YMIN = 0;
    private static final int YMAX = 1;
    private Node root = null;
    private int size;
    private Point2D nearestNeighbor;

    private class Node {
        // the point
        private Point2D p;

        // the axis-aligned rectangle corresponding to this node
        private RectHV rect;

        // the left/bottom subtree
        private Node lb = null;

        // the right/top subtree
        private Node rt = null;
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

        this.root = insert(this.root, p, XMIN, YMIN, XMAX, YMAX, true);

    }

    private Node insertNewNode(Point2D p, RectHV rectangle) {
        Node newNode = new Node();
        newNode.rect = rectangle;
        newNode.p = p;
        this.size++;
        return newNode;
    }

    // @note rectangle = RectHV(xmin, ymin, xmax, ymax)
    private Node insert(Node node, Point2D p, double xmin, double ymin, double xmax, double ymax, boolean evenLevel) {
        // Insert when you reach an empty location
        if (node == null) {
            return insertNewNode(p, new RectHV(xmin, ymin, xmax, ymax));
        }

        // If the point already exists, just return
        else if (node.p.equals(p)) {
            return node;
        }

        if (evenLevel) {

            if (p.x() <= node.p.x())
                // grader complains about creating RectHV on the stack when it may notbe used
                // for insertion.
                // Object creation is expensive, so we will create RectHV object at the time of
                // insertion.
                // old:
                // node.lb = insert(
                // node.lb,
                // p,
                // new RectHV(rectangle.xmin(), rectangle.ymin(), node.p.x(), rectangle.ymax()),
                // !evenLevel
                // );
                node.lb = insert(node.lb, p, xmin, ymin, node.p.x(), ymax, !evenLevel);
            else
                node.rt = insert(node.rt, p, node.p.x(), ymin, xmax, ymax, !evenLevel);
        }
        // The current node is horizontal: compare y-coordinates
        else {

            if (p.y() <= node.p.y())
                node.lb = insert(node.lb, p, xmin, ymin, xmax, node.p.y(), !evenLevel);
            else
                node.rt = insert(node.rt, p, xmin, node.p.y(), xmax, ymax, !evenLevel);
        }
        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }

        if (this.nearest(p) == null) {
            return (false);
        } else {
            return (this.nearest(p).equals(p));
        }

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

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("A rectangle needs to be supplied as an argument");
        }

        Queue<Point2D> allPoints = new Queue<Point2D>();
        this.collectPoints(root, rect, allPoints);

        return allPoints;
    }

    private void collectPoints(Node node, RectHV queryRectangle, Queue<Point2D> allPoints) {
        if (node == null) {
            return;
        }

        // if the query rectangle does not intersect the rectangle corresponding to a
        // node, there is no need to explore that node (or its subtrees).
        if (queryRectangle.intersects(node.rect)) {
            if (queryRectangle.contains(node.p)) {
                allPoints.enqueue(node.p);
            }
            // A subtree is searched only if it might contain a point contained in the query
            // rectangle.
            if (node.lb != null && node.lb.rect.intersects(queryRectangle)) {
                collectPoints(node.lb, queryRectangle, allPoints);
            }

            if (node.rt != null && node.rt.rect.intersects(queryRectangle)) {
                collectPoints(node.rt, queryRectangle, allPoints);
            }

        }
    }

    /**
     * @return a nearest neighbor in the set to point p; null if the set is empty
     */
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }

        if (this.isEmpty()) {
            return null;
        } else {
            this.nearestNeighbor = this.root.p;
            this.findNearestNeighborInSubTree(root, p, true);
            return this.nearestNeighbor;
        }
    }

    private void findNearestNeighborInSubTree(Node node, Point2D p, boolean evenLevel) {

        if (node == null) {
            return;
        }

        double closestDistance = this.nearestNeighbor.distanceSquaredTo(p);

        // if the closest point discovered so far is closer than the distance between
        // the query point and the rectangle corresponding to a node, there is no need
        // to explore that node (or its subtrees)
        if (closestDistance > node.rect.distanceSquaredTo(p)) {
            // explore this node
            if (closestDistance > p.distanceSquaredTo(node.p)) {
                this.nearestNeighbor = node.p;
            }

            // explore node's subtrees
            // when there are two possible subtrees to go down, you always choose the
            // subtree that is on the same side of the splitting line as the query point as
            // the *first* subtree to explore (and then explore the second subtree): the
            // closest point found while exploring the
            // first subtree may enable pruning of the second subtree.
            // we have left and right or top and bottom splits (or lines)
            // prioritizing order to exploration saves compute time associated to
            // p.distanceSquaredTo(node.p) in lines above.
            if ((evenLevel && (p.x() <= node.p.x())) || (!evenLevel && (p.y() <= node.p.y()))) {
                // ... query point as the *first* subtree to explore
                this.findNearestNeighborInSubTree(node.lb, p, !evenLevel);

                // ... and then explore the second subtree
                this.findNearestNeighborInSubTree(node.rt, p, !evenLevel);
            } else {
                // ... query point as the *first* subtree to explore
                this.findNearestNeighborInSubTree(node.rt, p, !evenLevel);

                // ... and then explore the second subtree
                this.findNearestNeighborInSubTree(node.lb, p, !evenLevel);
            }

        }

    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // intentionally left blank
    }
}
