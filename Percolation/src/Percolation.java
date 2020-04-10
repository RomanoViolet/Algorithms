import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final int n;
    private final Tree tree;

    private class AddressType {

        int row = 0;
        int column = 0;

        AddressType() {
            row = 0;
            column = 0;
        }

    }

    private class Tree {

        CellType[][] grid;
        int totalOpenCells = 0;
        WeightedQuickUnionUF uf;
        int n = 0;

        private class CellType {
            boolean isOpen = false;
            boolean isOccupied = false;

            CellType() {
            }

            public void open() {
                this.isOpen = true;
            }

        }

        Tree(int n) {
            // declare
            this.grid = new CellType[n][n];

            // initialize
            for (int row = 0; row < n; ++row) {
                for (int col = 0; col < n; ++col) {
                    this.grid[row][col] = new CellType();
                }
            }

            // n*n cells as required by the matrix. nth cell as special "start" cell,
            // (n+1)th cell as special "stop" cell.
            this.uf = new WeightedQuickUnionUF(n * n + 2);
            this.n = n;

        }

        void openACell(int row, int col) {
            this.grid[row][col].open();
            this.totalOpenCells++;

            // connect it to the left cell if it is unblocked
            if ((col - 1 >= 0) && (this.isACellOpen(row, col - 1))) {
                this.uf.union(this.rowColToIndex(row, col), this.rowColToIndex(row, col - 1));
            }

            // connect it to the right cell if it is unblocked
            if ((col + 1 <= this.n - 1) && (this.isACellOpen(row, col + 1))) {
                this.uf.union(this.rowColToIndex(row, col), this.rowColToIndex(row, col + 1));
            }

            // connect it to the top cell if it is unblocked
            if ((row - 1 >= 0) && (this.isACellOpen(row - 1, col))) {
                this.uf.union(this.rowColToIndex(row, col), this.rowColToIndex(row - 1, col));
            }

            // connect it to the bottom cell if it is unblocked
            if ((row + 1 <= this.n - 1) && (this.isACellOpen(row + 1, col))) {
                this.uf.union(this.rowColToIndex(row, col), this.rowColToIndex(row + 1, col));
            }

            // if this is a cell on the top row, connect it to the special "start" cell
            if (row == 0) {
                this.uf.union(this.n * this.n, this.rowColToIndex(row, col));
            }

            // if this is a cell on the bottom row, connect it to the special "end" cell
            if (row == this.n - 1) {
                this.uf.union((this.n * this.n) + 1, this.rowColToIndex(row, col));
            }
        }

        boolean isFull(int row, int col) {
            // row and column start at 0.
            int p = this.rowColToIndex(row, col);
            int q = this.n * this.n;
            return (this.uf.find(p) == this.uf.find(q));
        }

        void openACell(int index) {

            AddressType a;
            a = this.indexToRowColumn(index);
            this.openACell(a.row - 1, a.column - 1);

        }

        boolean isACellOpen(int row, int col) {
            return (this.grid[row][col].isOpen);
        }

        boolean isACellOpen(int index) {
            AddressType a;
            a = this.indexToRowColumn(index);
            return (this.isACellOccupied(a.row - 1, a.column - 1));

        }

        boolean isACellOccupied(int row, int col) {
            return (this.grid[row][col].isOccupied);
        }

        int totalOpenCells() {
            return (this.totalOpenCells);
        }

        boolean areEndsConnected() {
            int p = this.n * this.n;
            int q = p + 1;
            return (this.uf.find(p) == this.uf.find(q));
        }

        // private helpers
        int rowColToIndex(int row, int col) {
            // indices start at 0.
            return (((row) * this.n) + (col));
        }

        public AddressType indexToRowColumn(int index) {
            AddressType a = new AddressType();

            // since in the setup rows start with 1.
            a.row = (index / this.n) + 1;
            a.column = index - (((a.row - 1) * this.n)) + 1;

            return (a);
        }

    } // private class Tree

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must not be negative");
        }
        this.n = n;
        this.tree = new Tree(n);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        // Throw an IllegalArgumentException if any argument to open() is outside of
        // prescribed range
        // In the problem indices start from 1.
        if ((row > this.n) || (col > this.n)) {
            throw new ArrayIndexOutOfBoundsException("Requested (row, column) address out of bounds.");
        }

        this.tree.openACell(row - 1, col - 1);

    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if ((row > this.n) || (col > this.n)) {
            throw new ArrayIndexOutOfBoundsException("Requested (row, column) address out of bounds.");
        }
        return this.tree.isACellOpen(row - 1, col - 1);

    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if ((row > this.n) || (col > this.n) || (col < 1) || (row < 1)) {
            throw new ArrayIndexOutOfBoundsException("Requested (row, column) address out of bounds.");
        }
        return (this.tree.isFull(row - 1, col - 1));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return (this.tree.totalOpenCells());
    }

    // does the system percolate?
    public boolean percolates() {
        return (this.tree.areEndsConnected());
    }

    // run one experiment for percolate.
    private static double runPercolate(int gridSize) {
        double ratioOfOpenSites = 0;
        // long seed = StdRandom.getSeed();
        // long seed = System.currentTimeMillis();
        // StdRandom.setSeed(seed);
        Percolation p = new Percolation(gridSize);
        AddressType a;
        int siteTobeUnblocked = 0;
        while (!p.percolates()) {
            // choose a random site
            siteTobeUnblocked = StdRandom.uniform(p.n * p.n);
            // System.out.println("Opening site: " + siteTobeUnblocked);
            // convert to row-column format
            a = p.tree.indexToRowColumn(siteTobeUnblocked);

            // if this is already open, then continue to the next round

            if (!p.tree.isACellOpen(a.row - 1, a.column - 1)) {
                p.tree.openACell(a.row - 1, a.column - 1);
            } else {
                continue;
            }

            // System.out.println("Opened cells: " + p.tree.totalOpenCells() + " of " +
            // (p.n * p.n));
        }

        ratioOfOpenSites = p.tree.totalOpenCells() / ((double) p.n * p.n);
        // System.out.println("Ratio: " + ratioOfOpenSites);
        return ratioOfOpenSites;
    }

    // test client (optional)
    public static void main(String[] args) {
        double ratio = Percolation.runPercolate(100);
        System.out.println("Ratio: " + ratio);
    }

}
