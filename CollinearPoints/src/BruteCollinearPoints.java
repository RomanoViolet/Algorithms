public class BruteCollinearPoints {

    private Point[] points;
    private final int POINTS_EXAMINED_FOR_COLLINEARITY = 4;
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
        this.points = points;
    }

    private Point[] sortPoints(Point[] points) {
        Point[] sortedPoints = new Point[POINTS_EXAMINED_FOR_COLLINEARITY];

        // initialize
        for (int i = 0; i < points.length; ++i) {
            sortedPoints[i] = points[i];
        }

        // insertion sort
        for (int i = 0; i < sortedPoints.length; ++i) {
            for (int j = i; j > 0; j--) {
                // if points[j] < points[j-1]
                if (sortedPoints[j].compareTo(sortedPoints[j - 1]) == -1) {
                    Point temp = points[j];
                    points[j] = points[j - 1];
                    points[j - 1] = temp;
                } else {
                    break;
                }
            }
        }

        return (sortedPoints);
    }

    private boolean arePointsCollinear(Point[] points) {
        return ((points[0].slopeTo(points[1]) == points[0].slopeTo(points[2]))
                && (points[0].slopeTo(points[1]) == points[0].slopeTo(points[3])));
    }

    private LineSegment GetLineSegment(Point[] sortedPoints) {
        LineSegment segment = new LineSegment(sortedPoints[0], sortedPoints[3]);
        return (segment);
    }

    private void GenerateOneCombination(Point[] arr, int k, int startPosition, Point[] result) {
        // adapted from https://stackoverflow.com/a/16256122
        if (k == 0) {
            this.nCombinations++;
            Point[] sortedPoints = this.sortPoints(result);
            if (this.arePointsCollinear(sortedPoints)) {
                Node newHead = new Node();
                newHead.thisPoint = this.GetLineSegment(sortedPoints);
                newHead.nextTowardsTail = this.head;
                this.head = newHead;
                this.size++;
            }
            return;
        }
        for (int i = startPosition; i <= arr.length - k; i++) {
            result[result.length - k] = arr[i];
            GenerateOneCombination(arr, k - 1, i + 1, result);
        }

    }

    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; ++i) {
            result = result * i;
        }
        return (result);
    }

    private void GenerateAllCombinationOfPoints(int k) {
        Point[] result = new Point[k];
        this.GenerateOneCombination(this.points, k, 0, result);
        int n = this.points.length;
        long expectedNumberOfCombinations = this.factorial(n) / (this.factorial(k) * this.factorial(n - k));
        assert (expectedNumberOfCombinations == this.nCombinations) : "Number of combinations generated is incorrect";
    }

    private LineSegment[] generateArrayOfLineSegments() {
        LineSegment[] arrayOfLineSegments = new LineSegment[this.size];
        int numberOfSegments = 0;
        while (this.head.nextTowardsTail != null) {
            arrayOfLineSegments[numberOfSegments] = this.head.thisPoint;
            this.head = this.head.nextTowardsTail;
            numberOfSegments++;
        }

        assert (numberOfSegments == this.size) : "Incorrect number of segments collected";
        return (arrayOfLineSegments);
    }

    // the number of line segments
    public int numberOfSegments() {
        return this.size;
    }

    // the line segments
    public LineSegment[] segments() {
        this.GenerateAllCombinationOfPoints(4);
        LineSegment[] allSegments = this.generateArrayOfLineSegments();
        return (allSegments);

    }

}
