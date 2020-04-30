public class FastCollinearPoints {

    private final Point[] points;
    private LineSegment[] uniqueSegments;

    // number of line segments found
    private int size = 0;

    private class SegmentsWithMinPoint implements Comparable<SegmentsWithMinPoint> {
        private LineSegment segment;
        private Point minPoint;
        private double slope;

        // compare by min point
        public int compareTo(SegmentsWithMinPoint that) {
            if (this.minPoint.compareTo(that.minPoint) < 0) {
                return -1;
            }
            if (this.minPoint.compareTo(that.minPoint) > 0) {
                return +1;
            }
            return 0;
        }
    }

    private class Node implements Comparable<Node> {
        private SegmentsWithMinPoint segmentWithMinPoint = null;
        private Node nextTowardsTail = null;
        private double slope;
        private Point thisPoint;

        // compare by slope
        public int compareTo(Node that) {

            if (Double.compare(this.slope, that.slope) == -1)
                return -1;
            if (Double.compare(this.slope, that.slope) == 1)
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
        this.points = points.clone();
        Point[] aux = new Point[this.points.length];
        this.mergeSort(this.points, aux, 0, this.points.length - 1);

        // points are already sorted
        if (this.doDuplicatesExist(this.points)) {
            throw new IllegalArgumentException("Duplicate Points provided");
        }

        // find all segments
        this.findAllSegments(this.points);

        // find all unique segments
        this.removeDuplicateSegments();
    }

    private void removeDuplicateSegments() {

        if (this.size == 0) {
            // return a 0-length array. Not null.
            this.uniqueSegments = new LineSegment[this.size];
            return;
        }

        Node[] allSegments = new Node[this.size];
        int i = 0;
        // remove all segments with the same slope and the same min point

        // convert from linkedList to Array for sorting.
        while (this.head.nextTowardsTail != null) {
            allSegments[i] = new Node();
            SegmentsWithMinPoint segmentWithMinPoint = new SegmentsWithMinPoint();

            segmentWithMinPoint.segment = this.head.segmentWithMinPoint.segment;
            segmentWithMinPoint.minPoint = this.head.segmentWithMinPoint.minPoint;

            allSegments[i].slope = this.head.segmentWithMinPoint.slope;
            allSegments[i].segmentWithMinPoint = segmentWithMinPoint;

            this.head = this.head.nextTowardsTail;
            i++;
        }

        int nElementsInAllSegmentsArray = i;

        // cleanup
        for (int j = i; j < nElementsInAllSegmentsArray; ++j) {
            allSegments[j] = null;
        }

        // we are done with initial i here.

        // sort based on slope
        Node[] aux = new Node[nElementsInAllSegmentsArray];
        this.mergeSort(allSegments, aux, 0, nElementsInAllSegmentsArray - 1);
        aux = null;

        // within sorted by slope, sort again by minPoints.
        // Exploit mergesort's property of stability.
        // segment, slope, minPoint.
        // first creat an array which will then be passed onto mergesort.
        SegmentsWithMinPoint[] segmentsSoredByMinPoints = new SegmentsWithMinPoint[nElementsInAllSegmentsArray];
        for (int k = 0; k < nElementsInAllSegmentsArray; ++k) {
            segmentsSoredByMinPoints[k] = new SegmentsWithMinPoint();
            segmentsSoredByMinPoints[k].minPoint = allSegments[k].segmentWithMinPoint.minPoint;
            segmentsSoredByMinPoints[k].segment = allSegments[k].segmentWithMinPoint.segment;
            segmentsSoredByMinPoints[k].slope = allSegments[k].slope;
        }

        SegmentsWithMinPoint[] auxSegmentsSoredByMinPoints = new SegmentsWithMinPoint[nElementsInAllSegmentsArray];
        this.mergeSort(segmentsSoredByMinPoints, auxSegmentsSoredByMinPoints, 0, nElementsInAllSegmentsArray - 1);
        auxSegmentsSoredByMinPoints = null;

        // remove duplicates. Duplicates are defined to be elements with the same slope
        // and same minPoint
        int numberOfUniqueSegments = nElementsInAllSegmentsArray;
        for (int k = 1; k < nElementsInAllSegmentsArray; ++k) {
            if ((segmentsSoredByMinPoints[k] != null)
                    && (Double.compare(segmentsSoredByMinPoints[k].slope, segmentsSoredByMinPoints[k - 1].slope) == 0)
                    && (segmentsSoredByMinPoints[k].minPoint
                            .compareTo(segmentsSoredByMinPoints[k - 1].minPoint) == 0)) {
                // duplicate segment. Move it to the end of the array

                // move the duplicate segment to the end of the array.
                segmentsSoredByMinPoints[k - 1] = null;
                // allSegments[numberOfUniqueSegments - 1] = null;
                numberOfUniqueSegments--;
            }
        }

        this.uniqueSegments = new LineSegment[numberOfUniqueSegments];
        int finalIndexOfSegment = 0;
        for (int currentUniqueSegmentNumber = 0; currentUniqueSegmentNumber < segmentsSoredByMinPoints.length; ++currentUniqueSegmentNumber) {
            if (segmentsSoredByMinPoints[currentUniqueSegmentNumber] == null) {
                continue;
            }
            this.uniqueSegments[finalIndexOfSegment] = segmentsSoredByMinPoints[currentUniqueSegmentNumber].segment;
            finalIndexOfSegment++;
        }

        // Correct the total number of unique segments
        this.size = numberOfUniqueSegments;

    }

    private void findAllSegments(Point[] arg) {

        // take each point, and examine the slope made by every other point to this
        // point.
        // report all points which make the same slope.
        // Slope values may be repeated, e.g., s1, s1, s2, s3, s3, ...
        // Requirement: A line should contain 4 or more points, exactly once.
        for (int i = 0; i < arg.length; ++i) {

            // points[i] is the reference point.

            Node[] n = new Node[arg.length];
            for (int j = 0; j < arg.length; ++j) {
                n[j] = new Node();
                n[j].slope = arg[i].slopeTo(arg[j]);
                // a slope of +s and -s are still on the same line, just different direction.
                // for simplicity, we convert all slopes to +ve number.
                // Exception: -Inf since it is expected to be at the beginning of the array, and
                // it corresponds to reference point.
                // if ((n[j].slope < 0) && (n[j].slope != Double.NEGATIVE_INFINITY)) {
                // n[j].slope = -1 * n[j].slope;
                // }
                // record the point from which the slope to points[i] is calculated.
                n[j].thisPoint = arg[j];
            }

            Node[] aux = new Node[n.length];

            // points were sorted by point, now sort by slope.
            // mergesort is stable: sorting by points should not be lost due to sorting by
            // slope.
            this.mergeSort(n, aux, 0, n.length - 1);
            aux = null;

            // after mergesort, the slope of -∞ and correponding reference node will be
            // first element of the array
            assert (n[0].slope == Double.NEGATIVE_INFINITY) : "Sorting by slope is incorrect";
            assert (n[0].thisPoint.compareTo(arg[i]) == 0) : "First point in n is not the reference point, as expected";
            // since duplicates have been removed, there will no other point with slope of
            // -∞

            // find all lines where points[i] participates
            // done by counting the continuous run of each slope
            int runningCounter = 1;
            Point startingPoint = n[1].thisPoint;
            for (int k = 2; k < n.length; ++k) {
                // -0 != 0 for Doubles. For algorithm, it is.
                if ((Double.compare(n[k].slope, n[k - 1].slope) == 0) || ((n[k].slope == 0) && (n[k - 1].slope == -0))
                        || ((n[k].slope == -0) && (n[k - 1].slope == 0))) {
                    runningCounter++;

                } else {
                    // reference point is to be added separately
                    if (runningCounter >= 3) {
                        addSegment(arg, i, n, startingPoint, k);

                    }
                    runningCounter = 1;
                    startingPoint = n[k].thisPoint;
                }
            }

            // corner case, run of same slopes continues till the end, so it is not caught
            // in the loop above.
            int k = n.length;
            if (runningCounter >= 3) {
                addSegment(arg, i, n, startingPoint, k);
            }
        }

    }

    private void addSegment(Point[] arg, int i, Node[] n, Point startingPoint, int k) {
        LineSegment newSegment;
        Point minPoint;
        Point maxPoint;

        // found 3 points which lie on a straight line through the reference point
        // check whether the reference point lies on the end of the chosen points
        // exploit the stability of mergesort to compare only two points
        if (arg[i].compareTo(startingPoint) < 0) {
            // reference point is the smallest in the points with the same slope
            newSegment = new LineSegment(arg[i], n[k - 1].thisPoint);
            minPoint = arg[i];
            maxPoint = n[k - 1].thisPoint;
        } else if (arg[i].compareTo(n[k - 1].thisPoint) > 0) {
            // reference point is the largest in the points with the same slope
            newSegment = new LineSegment(startingPoint, arg[i]);
            minPoint = startingPoint;
            maxPoint = arg[i];
        } else {
            // reference point lies somewhere in between
            newSegment = new LineSegment(startingPoint, n[k - 1].thisPoint);
            minPoint = startingPoint;
            maxPoint = n[k - 1].thisPoint;
        }

        // record this segment
        Node newHead = new Node();
        SegmentsWithMinPoint segmentWithMinPoint = new SegmentsWithMinPoint();
        segmentWithMinPoint.segment = newSegment;

        segmentWithMinPoint.minPoint = minPoint;
        segmentWithMinPoint.slope = minPoint.slopeTo(maxPoint);
        newHead.segmentWithMinPoint = segmentWithMinPoint;
        newHead.nextTowardsTail = this.head;
        this.head = newHead;
        this.size++;
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
        for (int i = 1; i < arg.length; ++i) {
            if (arg[i].compareTo(arg[i - 1]) == 0) {
                duplicatesExist = true;
                break;
            }
        }
        return (duplicatesExist);
    }

    private <T extends Comparable<? super T>> boolean isSorted(T[] arg, int lo, int hi) {
        boolean isSorted = true;

        for (int i = lo + 1; i <= hi; ++i) {
            if (arg[i - 1].compareTo(arg[i]) > 0) {
                isSorted = false;
                break;
            }
        }
        return (isSorted);
    }

    private <T extends Comparable<? super T>> void merge(T[] arg, T[] aux, int lo, int mid, int hi) {
        assert isSorted(arg, lo, mid);
        assert isSorted(arg, mid + 1, hi);

        // copy into aux array
        for (int i = lo; i <= hi; ++i) {
            aux[i] = arg[i];
        }

        // initialize the pointers to firsst elements of both half-arrays to be merged
        int i = lo;
        int j = mid + 1;

        // merge elements by comparing across half-sorted arrays
        for (int k = lo; k <= hi; ++k) {
            // k points to the element in the sorted full array

            // if the elements in the first half are exhausted
            if (i > mid) {
                arg[k] = aux[j++];
            }
            // if the elements in the second half are exhausted
            else if (j > hi) {
                arg[k] = aux[i++];
            }
            // compare elements across the halves
            else if (aux[j].compareTo(aux[i]) < 0) {
                arg[k] = aux[j++];
            } else {
                arg[k] = aux[i++];
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
        LineSegment[] result = this.uniqueSegments.clone();
        return (result);
    }

}
