public class BruteCollinearPoints {

    private final Point[] points;
    private int nCombinations = 0;

    private class Node {
        private LineSegment thisPoint = null;
        private Node nextTowardsTail = null;
    }

    private Node head = null;
    private int size = 0;

    // finds all line segments containing 4 points
    // to be instantiated with a list of <x,y> tuples.
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalAccessError("No Points Supplied");
        }

        if (this.isAnyPointNull(points)) {
            throw new IllegalArgumentException("A null point provided");
        }

        if (this.doDuplicatesExist(points)) {
            throw new IllegalArgumentException("Duplicate Points provided");
        }
        this.points = points.clone();
    }

    private boolean isAnyPointNull(Point[] arg) {
        boolean nullPointsExist = false;
        for (int i = 0; i < arg.length; ++i) {
            if (arg[i] == null) {
                nullPointsExist = true;
                break;
            }
        }

        return (nullPointsExist);
    }

    private boolean doDuplicatesExist(Point[] arg) {
        boolean duplicatesExist = false;
        Point[] sortedPoints = new Point[arg.length];
        sortedPoints = this.sortPoints(arg);
        for (int i = 1; i < sortedPoints.length; ++i) {
            if (sortedPoints[i].compareTo(sortedPoints[i - 1]) == 0) {
                duplicatesExist = true;
                break;
            }
        }

        return (duplicatesExist);
    }

    private Point[] sortPoints(Point[] arg) {
        Point[] sortedPoints = new Point[arg.length];

        // initialize
        for (int i = 0; i < arg.length; ++i) {
            sortedPoints[i] = arg[i];
        }

        // insertion sort
        for (int i = 0; i < sortedPoints.length; ++i) {
            for (int j = i; j > 0; j--) {
                // if points[j] < points[j-1]
                if (sortedPoints[j].compareTo(sortedPoints[j - 1]) < 0) {
                    Point temp = sortedPoints[j];
                    sortedPoints[j] = sortedPoints[j - 1];
                    sortedPoints[j - 1] = temp;
                } else {
                    break;
                }
            }
        }

        return (sortedPoints);
    }

    private boolean arePointsCollinear(Point[] arg) {
        return ((arg[0].slopeTo(arg[1]) == arg[0].slopeTo(arg[2]))
                && (arg[0].slopeTo(arg[1]) == arg[0].slopeTo(arg[3])));
    }

    private LineSegment getLineSegment(Point[] sortedPoints) {
        LineSegment segment = new LineSegment(sortedPoints[0], sortedPoints[3]);
        return (segment);
    }

    private void generateOneCombination(Point[] arr, int k, int startPosition, Point[] result) {
        // adapted from https://stackoverflow.com/a/16256122
        if (k == 0) {
            this.nCombinations++;
            Point[] sortedPoints = this.sortPoints(result);
            if (this.arePointsCollinear(sortedPoints)) {
                Node newHead = new Node();
                newHead.thisPoint = this.getLineSegment(sortedPoints);
                newHead.nextTowardsTail = this.head;
                this.head = newHead;
                this.size++;
            }
            return;
        }
        for (int i = startPosition; i <= arr.length - k; i++) {
            result[result.length - k] = arr[i];
            generateOneCombination(arr, k - 1, i + 1, result);
        }

    }

    private double factorial(int n) {
        double result = 1;
        for (int i = 2; i <= n; ++i) {
            result = result * i;
        }
        return (result);
    }

    private void generateAllCombinationOfPoints(int k) {
        Point[] result = new Point[k];
        this.generateOneCombination(this.points, k, 0, result);
        int n = this.points.length;
        double expectedNumberOfCombinations = this.factorial(n) / (this.factorial(k) * this.factorial(n - k));

        assert ((expectedNumberOfCombinations - this.nCombinations) < 1 && (expectedNumberOfCombinations
                - this.nCombinations) > -1) : "Number of combinations generated is incorrect";
    }

    private LineSegment[] generateArrayOfLineSegments() {
        LineSegment[] arrayOfLineSegments = new LineSegment[this.size];
        int numberOfSegments = 0;
        if (this.head == null) {
            LineSegment[] emptyResult = new LineSegment[0];
            return (emptyResult);
        }
        while (this.head.nextTowardsTail != null) {
            arrayOfLineSegments[numberOfSegments] = this.head.thisPoint;
            this.head = this.head.nextTowardsTail;
            numberOfSegments++;
        }

        // last node
        arrayOfLineSegments[numberOfSegments] = this.head.thisPoint;

        assert ((numberOfSegments + 1) == this.size) : "Incorrect number of segments collected";
        return (arrayOfLineSegments);
    }

    // the number of line segments
    public int numberOfSegments() {
        return this.size;
    }

    // the line segments
    public LineSegment[] segments() {
        this.generateAllCombinationOfPoints(4);
        LineSegment[] allSegments = this.generateArrayOfLineSegments();
        return (allSegments);
    }

}
