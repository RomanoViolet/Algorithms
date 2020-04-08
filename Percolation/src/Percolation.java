import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private int _n = 0;
    private int _latticeID = 0;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n)
    {
        if (n <= 0)
        {
            throw new IllegalArgumentException("n must not be negative");
        }
        this._n = n;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col)
    {
        // Throw an IllegalArgumentException if any argument to open() is outside of prescribed range


    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col)
    {
        return false;

    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col)
    {
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites()
    {
        return 0;
    }

    // does the system percolate?
    public boolean percolates()
    {
        return false;
    }

    // private helpers
    private void rowColumntoIdentifier(int row, int col)
    { 
        // row and column must be correct.
        this._latticeID = ((row-1)*this._n) + col;

    }

    // test client (optional)
    public static void main(String[] args)
    {
        Percolation p = new Percolation(-1);
    }

}