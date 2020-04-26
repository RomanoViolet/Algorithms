public class FastCollinearPoints {

    private Point[] points;

    // number of line segments found
    private int size = 0;

    private class Node implements Comparable<Node> {
        private LineSegment thisSegment = null;
        private Node nextTowardsTail = null;
        private Double slope;
        private Point thisPoint;

        // compare by slope
        public int compareTo(Node that) {

            if (this.slope.compareTo(that.slope) == -1)
                return -1;
            if (this.slope.compareTo(that.slope) == 1)
                return +1;
            return 0;
        }
    }

    private Node head = null;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalAccessError("No Points Supplied");
        }

        if (this.isAnyPointNull(points)) {
            throw new IllegalArgumentException("A null point provided");
        }

        // sort the points, which is necessary for some methods.
        this.points = points;
        Point[] aux = new Point[this.points.length];
        this.mergeSort(this.points, aux, 0, this.points.length - 1);

        // points are already sorted
        if (this.doDuplicatesExist(this.points)) {
            throw new IllegalArgumentException("Duplicate Points provided");
        }

        // find all segments
        LineSegment[] allSegments = this.findAllSegments(this.points);
    }

    private LineSegment[] findAllSegments(Point[] points) {

        // take each point, and examine the slope made by every other point to this
        // point.
        // report all points which make the same slope.
        // Slope values may be repeated, e.g., s1, s1, s2, s3, s3, ...
        // Requirement: A line should contain 4 or more points, exactly once.
        for (int i = 0; i < points.length; ++i) {

            // points[i] is the reference point.

            Node[] n = new Node[points.length - 1];
            for (int j = 0; j < points.length; ++j) {
                n[j].slope = points[i].slopeTo(points[j]);
                // a slope of +s and -s are still on the same line, just different direction.
                // for simplicity, we convert all slopes to +ve number
                if (n[j].slope < 0) {
                    n[j].slope = -1 * n[j].slope;
                }
                // record the point from which the slope to points[i] is calculated.
                n[j].thisPoint = points[j];
            }

            Node[] aux = new Node[points.length - 1];

            // points were sorted by point, now sort by slope.
            // mergesort is stable: sorting by points should not be lost due to sorting by
            // slope.
            this.mergeSort(n, aux, 0, n.length - 1);
            aux = null;

            // after mergesort, the slope of -∞ and correponding reference node will be
            // first element of the array
            assert (n[0].slope == Double.NEGATIVE_INFINITY) : "Sorting by slope is incorrect";
            assert (n[0].thisPoint
                    .compareTo(points[i]) == 0) : "First point in n is not the reference point, as expected";
            // since duplicates have been removed, there will no other point with slope of
            // -∞

            // find all lines where points[i] participates
            // done by counting the continuous run of each slope
            int runningCounter = 1;
            Point startingPoint = n[1].thisPoint;
            for (int k = 2; k < n.length; ++k) {

                if (n[k].slope.compareTo(n[k - 1].slope) == 0) {
                    runningCounter++;

                } else {
                    // reference point is to be added separately
                    if (runningCounter >= 3) {
                        // found 3 points which lie on a straight line through the reference point
                        // check whether the reference point lies on the end of the chosen points
                        // exploit the stability of mergesort to compare only two points
                        if (points[i].compareTo(startingPoint) == -1) {

                        }
                    }

                    startingPoint = n[k].thisPoint;
                }
            }
        }

    }

    private boolean isAnyPointNull(Point[] points) {
        boolean nullPointsExist = false;
        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null) {
                nullPointsExist = true;
                break;
            }
        }
        return (nullPointsExist);
    }

    private boolean doDuplicatesExist(Point[] points) {
        boolean duplicatesExist = false;
        for (int i = 1; i < points.length; ++i) {
            if (points[i].compareTo(points[i - 1]) == 0) {
                duplicatesExist = true;
                break;
            }
        }
        return (duplicatesExist);
    }

    private <T extends Comparable<? super T>> boolean isSorted(T[] points, int lo, int hi) {
        boolean isSorted = true;

        for (int i = 1; i < points.length; ++i) {
            if (points[i - 1].compareTo(points[i]) == 1) {
                isSorted = false;
                break;
            }
        }
        return (isSorted);
    }

    private <T extends Comparable<? super T>> void merge(T[] points, T[] aux, int lo, int mid, int hi) {
        assert isSorted(points, lo, mid);
        assert isSorted(points, mid + 1, hi);

        // copy into aux array
        for (int i = lo; i <= hi; ++i) {
            aux[i] = points[i];
        }

        // initialize the pointers to firsst elements of both half-arrays to be merged
        int i = lo;
        int j = mid + 1;

        // merge elements by comparing across half-sorted arrays
        for (int k = lo; k <= hi; ++k) {
            // k points to the element in the sorted full array

            // if the elements in the first half are exhausted
            if (i > mid) {
                points[k] = aux[j++];
            }
            // if the elements in the second half are exhausted
            else if (j > hi) {
                points[k] = aux[i++];
            }
            // compare elements across the halves
            else if (aux[j].compareTo(aux[i]) == -1) {
                points[k] = aux[j++];
            } else {
                points[k] = aux[i++];
            }
        }
    }

    private <T extends Comparable<? super T>> void mergeSort(T[] a, T[] aux, int lo, int hi) {
        if (hi <= lo) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        this.mergeSort(a, aux, lo, mid);
        this.mergeSort(a, aux, mid + 1, hi);
        this.merge(a, aux, lo, mid, hi);

    }

    // the number of line segments
    public int numberOfSegments() {
        return this.size;
    }

    // the line segments
    public LineSegment[] segments() {

    }

    // TODO Add Iterable.

}
