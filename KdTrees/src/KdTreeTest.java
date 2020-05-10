import edu.princeton.cs.algs4.Point2D;

public class KdTreeTest {
    public static void main(String[] args) {
        KdTree t = new KdTree();
        Point2D p = new Point2D(0.7, 0.2);
        t.insert(p);
        t.insert(new Point2D(0.5, 0.4));
        t.insert(new Point2D(0.2, 0.3));
        t.insert(new Point2D(0.4, 0.7));
        t.insert(new Point2D(0.9, 0.6));
        System.out.println("Done");
    }
}
