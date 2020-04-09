import java.sql.Struct;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.UF;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private int _n = 0;
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
            boolean _isOccupied = false;

        }

        Tree(int n) {
            this._grid = new CellType[n][n];

            // n*n cells as required by the matrix. nth cell as special "start" cell,
            // (n+1)th cell as special "stop" cell.
            this._uf = new UF(n * n + 2);
            this._n = n;
        }

        void openACell(int row, int col) {
            this._grid[row][col]._isOpen = true;
            this._totalOpenCells++;

            // connect it to the left cell if it is unblocked
            if (this.isACellOpen(row, col - 1)) {
                this._uf.union(this.rowColToIndex(row, col), this.rowColToIndex(row, col - 1));
            }

            // connect it to the right cell if it is unblocked
            if (this.isACellOpen(row, col + 1)) {
                this._uf.union(this.rowColToIndex(row, col), this.rowColToIndex(row, col + 1));
            }

            // connect it to the top cell if it is unblocked
            if (this.isACellOpen(row - 1, col)) {
                this._uf.union(this.rowColToIndex(row, col), this.rowColToIndex(row - 1, col));
            }

            // connect it to the bottom cell if it is unblocked
            if (this.isACellOpen(row + 1, col)) {
                this._uf.union(this.rowColToIndex(row, col), this.rowColToIndex(row + 1, col));
            }

            // if this is a cell on the top row, connect it to the special "start" cell
            if (row == 0) {
                this._uf.union(this._n * this._n, this.rowColToIndex(row, col));
            }

            // if this is a cell on the bottom row, connect it to the special "end" cell
            if (row == this._n - 1) {
                this._uf.union((this._n * this._n) + 1, this.rowColToIndex(row, col));
            }
        }

        boolean isACellOpen(int row, int col) {
            return (this._grid[row][col]._isOpen);
        }

        boolean isACellOccupied(int row, int col) {
            return (this._grid[row][col]._isOccupied);
        }

        int totalOpenCells() {
            return (this._totalOpenCells);
        }

        boolean areEndsConnected() {
            int p = this._n * this._n;
            int q = p + 1;
            return (this._uf.find(p) == this._uf.find(q));
        }

        // private helpers
        int rowColToIndex(int row, int col) {
            return (((row - 1) * this._n) + (col - 1));
        }

        private CellType[][] _grid;
        private int _totalOpenCells = 0;
        private UF _uf;
        private int _n = 0;

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

        this._tree.openACell(row - 1, col - 1);

    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if ((row > this._n) || (col > this._n)) {
            throw new IllegalArgumentException("Requested (row, column) address out of bounds.");
        }
        return this._tree.isACellOpen(row - 1, col - 1);

    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if ((row > this._n) || (col > this._n)) {
            throw new IllegalArgumentException("Requested (row, column) address out of bounds.");
        }
        return (this._tree.isACellOccupied(row - 1, col - 1));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return (this._tree.totalOpenCells());
    }

    // does the system percolate?
    public boolean percolates() {
        return (this._tree.areEndsConnected());
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation p = new Percolation(-1);
    }

}
