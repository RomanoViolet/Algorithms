
import edu.princeton.cs.algs4.Bag

public class Board {

    private int[][] tiles;
    private final int dimension;
    private int manhattanDistance;
    private int hammingDistance;
    private Bag<Board> neighboringBoards;
    private TileCoordinates locationOfZero; // the empty space in the puzzle

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("Board is not initialized properly.");
        }
        this.tiles = tiles.clone();
        this.dimension = this.tiles.length;
        this.hammingDistance = this.computeHammingDistance();
        this.manhattanDistance = this.computeManhattanDistance();
    }

    // string representation of this board
    public String toString() {
        return new String("");
    }

    // board dimension n
    public int dimension() {
        return this.dimension;
    }

    public int hamming() {
        return (this.hammingDistance);
    }

    // number of tiles out of place
    private int computeHammingDistance() {
        // expected tile value: (row*dimension) + (col) + 1
        int hammingDistance = 0;
        int expectedTileValue = 0;
        TileCoordinates locationOfZero = new TileCoordinates();
        for (int row = 0; row < this.dimension; ++row) {
            for (int col = 0; col < this.dimension; ++col) {
                if (this.tiles[row][col] == 0) {
                    locationOfZero.col = col;
                    locationOfZero.row = row;
                    continue;
                }
                expectedTileValue = (row * this.dimension) + (col) + 1;
                hammingDistance = hammingDistance + ((expectedTileValue != this.tiles[row][col]) ? 1 : 0);
            }
        }
        this.locationOfZero = locationOfZero;
        return hammingDistance;
    }

    private class TileCoordinates {
        int row = 0;
        int col = 0;
    }

    private TileCoordinates TileValueToExpectedAddress(int value) {
        TileCoordinates coordinates = new TileCoordinates();

        // rely on integer division
        coordinates.row = (value - 1) / this.dimension;
        coordinates.col = value - (coordinates.row * this.dimension) - 1;

        return coordinates;

    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return (this.manhattanDistance);
    }

    private int computeManhattanDistance() {
        // manhattan: error in row + error in col
        int manhattanDistance = 0;
        TileCoordinates expectedCoordinates;
        int errorInRow = 0;
        int errorInCol = 0;
        for (int row = 0; row < this.dimension; ++row) {
            for (int col = 0; col < this.dimension; ++col) {
                if (this.tiles[row][col] == 0) {
                    continue;
                }
                expectedCoordinates = this.TileValueToExpectedAddress(this.tiles[row][col]);
                if (row > expectedCoordinates.row) {
                    errorInRow = row - expectedCoordinates.row;
                } else {
                    errorInRow = expectedCoordinates.row - row;
                }

                if (col > expectedCoordinates.col) {
                    errorInCol = col - expectedCoordinates.col;
                } else {
                    errorInCol = expectedCoordinates.col - col;
                }

                manhattanDistance = manhattanDistance + errorInCol + errorInRow;
            }
        }
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return (this.hammingDistance == 0);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this)
            return true;
        if (y == null)
            return false;
        if (y.getClass() != this.getClass())
            return false;
        Board that = (Board) y;

        boolean areBoardsSame = true;
        for (int row = 0; row < this.dimension; ++row) {
            for (int col = 0; col < this.dimension; ++col) {
                if (this.tiles[row][col] != that.tiles[row][col]) {
                    areBoardsSame = false;
                    break;
                }
            }
        }

        return areBoardsSame;
    }

    private Bag<Board> allNeighbors() {
        Bag<Board> neighbors;

        //
        // Model this as the blank "moving" left, right, top, or bottom.
        //

        // blank tile located on the top left corner
        if ((this.locationOfZero.col == 0) && (this.locationOfZero.row == 0)) {
            // New Board: Move tile from (0,1) to (0,0)
            // New Board: Move tile from (1,0) to (0,0)
        }

        // blank tile on the bottom left corner
        if ((this.locationOfZero.col == 0) && (this.locationOfZero.row == this.dimension - 1)) {
            // New Board: Move tile from (this.dimension-2,0) to (this.dimension-1,0)
            // New Board: Move tile from (this.dimension-1,1) to (this.dimension-1,0)
        }

        // blank tile on the left edge

    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return (this.neighboringBoards);
    }

    // // a board that is obtained by exchanging any pair of tiles
    // public Board twin() {

    // }

    // unit testing (not graded)
    public static void main(String[] args) {

    }

}
