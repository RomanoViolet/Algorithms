import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.StdRandom;

public class PercolationStats {

    private final double[] ratios;
    private final int trials;
    private final int n;

    // constructor
    public PercolationStats(int n, int trials) {
        ratios = new double[trials];
        this.trials = trials;
        this.n = n;
        for (int trialNumber = 0; trialNumber < trials; ++trialNumber) {
            // ratios[trialNumber] = Percolation.runPercolate(n);
            double ratioOfOpenSites = 0;
            Percolation p = new Percolation(n);
            int siteTobeUnblocked = 0;
            while (!p.percolates()) {
                // choose a random site
                siteTobeUnblocked = StdRandom.uniform(n * n);

                // convert to row-column format
                AddressType a;
                a = this.indexToRowColumn(siteTobeUnblocked);

                // if this is already open, then continue to the next round
                if (!p.isOpen(a.row, a.column)) {
                    p.open(a.row, a.column);
                } else {
                    continue;
                }

                // System.out.println("Opened cells: " + p._tree.totalOpenCells() + " of " +
                // (p.n * p.n));
            }

            ratioOfOpenSites = p.numberOfOpenSites() / ((double) n * n);
            // System.out.println("Ratio: " + ratioOfOpenSites);
            ratios[trialNumber] = ratioOfOpenSites;
        }
    }

    private class AddressType {

        int row = 0;
        int column = 0;

        AddressType() {
            row = 0;
            column = 0;
        }

    }

    private AddressType indexToRowColumn(int index) {
        AddressType a = new AddressType();

        // since in the setup rows start with 1.
        a.row = (index / this.n) + 1;
        a.column = index - (((a.row - 1) * this.n)) + 1;

        return (a);
    }

    public double mean() {
        return (StdStats.mean(ratios));
    }

    public double stddev() {
        return (StdStats.stddev(ratios));
    }

    public double confidenceLo() {
        return (this.mean() - (1.96 * this.stddev()) / (Math.sqrt(this.trials)));
    }

    public double confidenceHi() {
        return (this.mean() + (1.96 * this.stddev()) / (Math.sqrt(this.trials)));
    }

    // test client (optional)
    public static void main(String[] args) {
        int[] gridSizes = { 20, 40, 80, 160, 320, 640 };
        int nTrials = 100;
        for (int thisGridSize : gridSizes) {
            Stopwatch timer = new Stopwatch();
            PercolationStats s = new PercolationStats(thisGridSize, nTrials);
            double elapsedTime = timer.elapsedTime();
            System.out.printf("%-40s = %.10f\n", "mean", s.mean());
            System.out.printf("%-40s = %.10f\n", "stddev", s.stddev());
            System.out.printf("%-40s = [%.10f,  %.10f]\n", "95% confidence interval", s.confidenceLo(),
                    s.confidenceHi());
            System.out.printf("Time Elapsed: %.10f\n", elapsedTime);
        }

    }

}
