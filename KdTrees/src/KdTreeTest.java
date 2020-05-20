import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.SET;
import java.awt.Color;
import java.util.Vector;

import edu.princeton.cs.algs4.StdRandom;

public class KdTreeTest {
    public static void main(String[] args) {
        KdTree t = new KdTree();
        assert t.contains(new Point2D(0.6, 0.4)) == false : "Insertion failed";
        // for (int i = 0; i < 1000000; i++) {
        // double x = StdRandom.uniform(0.0, 1.0);
        // double y = StdRandom.uniform(0.0, 1.0);
        // Point2D p = new Point2D(x, y);
        // t.insert(p);
        // }

        // System.out.println(t.range(new RectHV(0, 0, 0.002, 0.002)));
        // System.out.println(r.range(new RectHV(0, 0, 0.002, 0.002)));

        // SET<Point2D> myResponse = new SET<Point2D>();
        // for (Point2D thisPoint : t.range(new RectHV(0, 0, 0.02, 0.002))) {
        // myResponse.add(thisPoint);
        // }

        // assert myResponse.isEmpty() : "Number of points did not match";
        // Point2D p = new Point2D(0.7, 0.2);
        // t.insert(p);
        // r.insert(p);

        t.insert(new Point2D(0.5, 0.4));
        assert t.contains(new Point2D(0.5, 0.4)) == true : "Insertion failed";
        assert t.contains(new Point2D(0.6, 0.4)) == false : "Insertion failed";

        // t.insert(new Point2D(0.2, 0.3));
        // r.insert(new Point2D(0.2, 0.3));

        // t.insert(new Point2D(0.4, 0.7));
        // r.insert(new Point2D(0.4, 0.7));

        // t.insert(new Point2D(0.9, 0.6));
        // r.insert(new Point2D(0.9, 0.6));

        // StdDraw.setPenRadius(0.01);
        // StdDraw.setPenColor(new Color(10, 10, 10));
        // t.draw();
        // System.out.println("Done");

        // RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        // StdDraw.enableDoubleBuffering();
        // KdTree kdtree = new KdTree();
        // while (true) {
        // if (StdDraw.isMousePressed()) {
        // double x = StdDraw.mouseX();
        // double y = StdDraw.mouseY();
        // StdOut.printf("%8.6f %8.6f\n", x, y);
        // p = new Point2D(x, y);
        // if (rect.contains(p)) {
        // StdOut.printf("%8.6f %8.6f\n", x, y);
        // kdtree.insert(p);
        // StdDraw.clear();
        // kdtree.draw();
        // StdDraw.show();
        // }
        // }
        // StdDraw.pause(20);
        // }

        System.out.println("Done");
    }
}
