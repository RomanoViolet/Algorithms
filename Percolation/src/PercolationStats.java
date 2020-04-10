import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {

    double[] ratios;
    int _trials;

    // constructor
    public PercolationStats(int n, int trials) {
        ratios = new double[trials];
        _trials = trials;
        for (int trialNumber = 0; trialNumber < trials; ++trialNumber) {
            ratios[trialNumber] = Percolation.runPercolate(n);
        }

    }

    public double mean() {
        return (StdStats.mean(ratios));
    }

    public double stddev() {
        return (StdStats.stddev(ratios));
    }

    public double confidenceLo() {
        return (this.mean() - (1.96 * this.stddev()) / (Math.sqrt(this._trials)));
    }

    public double confidenceHi() {
        return (this.mean() + (1.96 * this.stddev()) / (Math.sqrt(this._trials)));
    }

    // test client (optional)
    public static void main(String[] args) {
        int gridSizes[] = { 20, 40, 80, 160, 320, 640 };
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
