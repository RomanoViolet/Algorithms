import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private static final int XMIN = 0;
    private static final int XMAX = 1;
    private static final int YMIN = 0;
    private static final int YMAX = 1;
    private Node root = null;
    private int size;
    private Point2D nearestNeighbor;
    private double squaredDistanceToNearestNeighbor;

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

    private Node insertNewNode(Point2D p, RectHV rectangle) {
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

    private Node insert(Node node, Point2D p, boolean evenLevel, RectHV rectangle) {
        // if the node is empty
        if (node == null) {
            return (this.insertNewNode(p, rectangle));
        }

        // Is thie even level? 0, 2, 4, ...?
        if (evenLevel) {
            if (node.p.compareTo(p) == 0) {
                return node;
            }

            // parent rectangle is split top/bottom
            if (p.x() < node.p.x()) {
                node.lb = insert(node.lb, p, !evenLevel, getLeftSplitOfParentRectangle(node.rect, node.p));
            } else {
                node.rt = insert(node.rt, p, !evenLevel, getRightSplitOfParentRectangle(node.rect, node.p));
            }
        }

        if (!evenLevel) {
            if (node.p.compareTo(p) == 0) {
                return node;
            }
            // parent rectangle is split left/right
            if (p.y() < node.p.y()) {
                node.lb = insert(node.lb, p, evenLevel, getBottomSplitOfParentRectangle(node.rect, node.p));
            } else {
                node.rt = insert(node.rt, p, evenLevel, getTopSplitOfParentRectangle(node.rect, node.p));
            }
        }

        return node;

    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }
        return (this.nearest(p).compareTo(p) == 0);
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
        this.collectPoints(this.root, s, rect);

        return (s);
    }

    private void collectPoints(Node node, SET<Point2D> s, RectHV rect) {
        if (node == null) {
            return;
        }
        if (node.rect.intersects(rect)) {
            // this rectangle may contain points of interest
            if (rect.contains(node.p)) {
                s.add(node.p);
            }
            this.collectPoints(node.lb, s, rect);
            this.collectPoints(node.rt, s, rect);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    // Cost: Linear in the size of this.points
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("A point needs to be supplied as an argument");
        }

        this.nearestNeighbor = this.root.p;
        this.squaredDistanceToNearestNeighbor = this.root.p.distanceSquaredTo(p);

        if (this.root != null) {
            this.findNearestNeighborInSubTree(this.root, p);
        }

        return (this.nearestNeighbor);
    }

    private void findNearestNeighborInSubTree(Node node, Point2D p) {

        if (node == null) {
            return;
        }
        double tempDistance;
        // Is it the left subtree to explore first?
        if (node.lb != null && node.lb.rect.contains(p)) {
            // yes
            tempDistance = node.lb.p.distanceSquaredTo(p);
            if (tempDistance < this.squaredDistanceToNearestNeighbor) {
                this.nearestNeighbor = node.lb.p;
                this.squaredDistanceToNearestNeighbor = tempDistance;

                // explore left part of the subtree to refine the estimate
                if (node.lb != null) {
                    this.findNearestNeighborInSubTree(node.lb, p);
                }
            }
        } else if (node.rt != null) {
            // right subtree
            tempDistance = node.rt.p.distanceSquaredTo(p);
            if (tempDistance < this.squaredDistanceToNearestNeighbor) {
                this.nearestNeighbor = node.rt.p;
                this.squaredDistanceToNearestNeighbor = tempDistance;
                // explore right part of the subtree to refine the estimate
                if (node.rt != null) {
                    this.findNearestNeighborInSubTree(node.rt, p);
                }
            }
        }

    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // intentionally left blank
    }
}
