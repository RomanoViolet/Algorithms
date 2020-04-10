import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    // constructor
    public PercolationStats(int n, int trials) {
        // Percolation p = new Percolation(n);
        double ratio = Percolation.runPercolate(1);
    }

    public double mean() {
        return (0.0);
    }

    public double stddev() {
        return (0.0);
    }

    public double confidenceLo() {
        return (0.0);
    }

    public double confidenceHi() {
        return (0.0);
    }

    // test client (optional)
    public static void main(String[] args) {
        PercolationStats s = new PercolationStats(20, 30);
    }

}
