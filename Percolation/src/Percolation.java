import java.sql.Struct;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private int _n = 0;
    private int _latticeID = 0;
    Tree _tree;

    public class AddressType {
        AddressType() {
            _row = 0;
            _column = 0;
        }

        int _row = 0;
        int _column = 0;
    }

    public class Tree {
        public class CellType {
            CellType() {
            }

            boolean _isOpen = false;
            AddressType _myRoot = new AddressType();

        }

        Tree(int n) {
            _grid = new CellType[n][n];
        }

        void openACell(int row, int col) {
            this._grid[row - 1][col - 1]._isOpen = true;
            this._totalOpenCells++;
        }

        boolean isACellOpen(int row, int col) {
            return (this._grid[row - 1][col - 1]._isOpen);
        }

        int totalOpenCells() {
            return (this._totalOpenCells);
        }

        private CellType[][] _grid;
        private int _totalOpenCells = 0;

    }

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must not be negative");
        }
        this._n = n;
        this._tree = new Tree(n);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        // Throw an IllegalArgumentException if any argument to open() is outside of
        // prescribed range
        // In the problem indices start from 1.
        if ((row > this._n) || (col > this._n)) {
            throw new IllegalArgumentException("Requested (row, column) address out of bounds.");
        }

        this._tree.openACell(row, col);

    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if ((row > this._n) || (col > this._n)) {
            throw new IllegalArgumentException("Requested (row, column) address out of bounds.");
        }
        return this._tree.isACellOpen(row, col);

    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if ((row > this._n) || (col > this._n)) {
            throw new IllegalArgumentException("Requested (row, column) address out of bounds.");
        }
        return (!isOpen(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return (this._tree.totalOpenCells());
    }

    // does the system percolate?
    public boolean percolates() {
        return false;
    }

    // private helpers

    // test client (optional)
    public static void main(String[] args) {
        Percolation p = new Percolation(-1);
    }

}
