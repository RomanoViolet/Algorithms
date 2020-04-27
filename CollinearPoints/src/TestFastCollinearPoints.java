import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;

public class TestFastCollinearPoints {
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
        StdDraw.setXscale(minX - 100, maxX + 100);
        if (minY == maxY) {
            maxY = maxY + 1;
        }
        StdDraw.setYscale(minY - 100, maxY + 100);
        StdDraw.setPenColor(new Color(10, 10, 10));
        for (int i = 0; i < points.length; ++i) {
            points[i].draw();
        }

        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(new Color(173, 216, 230));
        FastCollinearPoints fastCollinearPoints = new FastCollinearPoints(points);
        LineSegment[] segments = fastCollinearPoints.segments();
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(new Color(173, 216, 230));
        for (int i = 0; i < segments.length; ++i) {
            segments[i].draw();
        }
        System.out.println("Done");

    }

}
