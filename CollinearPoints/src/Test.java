import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;

public class Test {
    public static void main(String[] args) {
        // Read an input text file
        String in = StdIn.readString();
        int n = Integer.parseInt(in);
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            String XString = StdIn.readString();
            String YString = StdIn.readString();
            int x = Integer.parseInt(XString);
            int y = Integer.parseInt(YString);
            points[i] = new Point(x, y);
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }

        }

        StdDraw.setPenRadius(0.01);
        StdDraw.setXscale(minX, maxX);
        StdDraw.setYscale(minY, maxY);
        StdDraw.setPenColor(new Color(10, 10, 10));
        for (int i = 0; i < points.length; ++i) {
            points[i].draw();
        }

        BruteCollinearPoints bruteForceCollinearPoints = new BruteCollinearPoints(points);
        LineSegment[] segments = bruteForceCollinearPoints.segments();
        for (int i = 0; i < segments.length; ++i) {
            segments[i].draw();
        }

    }

}
