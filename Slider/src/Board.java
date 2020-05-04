import java.util.Arrays;
import edu.princeton.cs.algs4.Bag;

public class Board {

    private final int[][] tiles;
    private final int dimension;
    private final int manhattanDistance;
    private final int hammingDistance;
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
        // String result = new String("");
        StringBuilder result = new StringBuilder();
        result.append(this.dimension + "\n");
        for (int row = 0; row < this.dimension; ++row) {
            for (int col = 0; col < this.dimension; ++col) {
                result.append((this.tiles[row][col]) + " ");
            }
            result.append("\n");
        }

        return (result.toString());
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
        int localHammingDistance = 0;
        int expectedTileValue = 0;
        TileCoordinates localLocationOfZero = new TileCoordinates();
        for (int row = 0; row < this.dimension; ++row) {
            for (int col = 0; col < this.dimension; ++col) {
                if (this.tiles[row][col] == 0) {
                    localLocationOfZero.col = col;
                    localLocationOfZero.row = row;
                    continue;
                }
                expectedTileValue = (row * this.dimension) + (col) + 1;
                localHammingDistance = localHammingDistance + ((expectedTileValue != this.tiles[row][col]) ? 1 : 0);
            }
        }
        this.locationOfZero = localLocationOfZero;
        return localHammingDistance;
    }

    private class TileCoordinates {
        int row = 0;
        int col = 0;
    }

    private TileCoordinates tileValueToExpectedAddress(int value) {
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
        int localManhattanDistance = 0;
        TileCoordinates expectedCoordinates;
        int errorInRow = 0;
        int errorInCol = 0;
        for (int row = 0; row < this.dimension; ++row) {
            for (int col = 0; col < this.dimension; ++col) {
                if (this.tiles[row][col] == 0) {
                    continue;
                }
                expectedCoordinates = this.tileValueToExpectedAddress(this.tiles[row][col]);
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

                localManhattanDistance = localManhattanDistance + errorInCol + errorInRow;
            }
        }
        return localManhattanDistance;
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

    private int[][] copyOfTiles() {
        int[][] copy = new int[this.dimension][this.dimension];
        for (int row = 0; row < this.dimension; ++row) {
            copy[row] = Arrays.copyOf(this.tiles[row], this.dimension);
        }

        return (copy);
    }

    private Board moveBlankDown() {
        // blank can be moved down if it is not at the bottom edge.
        if (this.locationOfZero.row == this.dimension - 1) {
            return (null);
        }
        int[][] newTiles = this.copyOfTiles();
        newTiles[this.locationOfZero.row][this.locationOfZero.col] = newTiles[this.locationOfZero.row
                + 1][this.locationOfZero.col];
        newTiles[this.locationOfZero.row + 1][this.locationOfZero.col] = 0;
        Board newBoard = new Board(newTiles);
        return (newBoard);
    }

    private Board moveBlankUp() {
        // blank can be moved up if it is not at the top edge.
        if (this.locationOfZero.row == 0) {
            return (null);
        }
        int[][] newTiles = this.copyOfTiles();
        newTiles[this.locationOfZero.row][this.locationOfZero.col] = newTiles[this.locationOfZero.row
                - 1][this.locationOfZero.col];
        newTiles[this.locationOfZero.row - 1][this.locationOfZero.col] = 0;
        Board newBoard = new Board(newTiles);
        return (newBoard);
    }

    private Board moveBlankRight() {
        // blank can be moved right if it is not at the right edge.
        if (this.locationOfZero.col == this.dimension - 1) {
            return (null);
        }
        int[][] newTiles = this.copyOfTiles();
        newTiles[this.locationOfZero.row][this.locationOfZero.col] = newTiles[this.locationOfZero.row][this.locationOfZero.col
                + 1];
        newTiles[this.locationOfZero.row][this.locationOfZero.col + 1] = 0;
        Board newBoard = new Board(newTiles);
        return (newBoard);
    }

    private Board moveBlankLeft() {
        // blank can be moved left if it is not at the left edge.
        if (this.locationOfZero.col == 0) {
            return (null);
        }
        int[][] newTiles = this.copyOfTiles();
        newTiles[this.locationOfZero.row][this.locationOfZero.col] = newTiles[this.locationOfZero.row][this.locationOfZero.col
                - 1];
        newTiles[this.locationOfZero.row][this.locationOfZero.col - 1] = 0;
        Board newBoard = new Board(newTiles);
        return (newBoard);
    }

    private Bag<Board> allNeighbors() {
        Bag<Board> localNeighboringBoards = new Bag<Board>();
        //
        // Model this as the blank "moving" left, right, top, or bottom.
        //

        // move blank down
        Board blankDown = this.moveBlankDown();
        if (blankDown != null) {
            localNeighboringBoards.add(blankDown);
        }

        // move blank up
        Board blankUp = this.moveBlankUp();
        if (blankUp != null) {
            localNeighboringBoards.add(blankUp);
        }

        // move blank left
        Board blankLeft = this.moveBlankLeft();
        if (blankLeft != null) {
            localNeighboringBoards.add(blankLeft);
        }

        // move blank right
        Board blankRight = this.moveBlankRight();
        if (blankRight != null) {
            localNeighboringBoards.add(blankRight);
        }
        return (localNeighboringBoards);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        this.neighboringBoards = this.allNeighbors();
        return (this.neighboringBoards);
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] newTiles = new int[this.dimension][this.dimension];
        int temp;
        Board twinBoard;

        // make a copy of the tiles
        for (int row = 0; row < this.dimension; ++row) {
            for (int col = 0; col < this.dimension; ++col) {
                newTiles[row][col] = this.tiles[row][col];
            }
        }
        // if the zero is on the left edge
        if (this.locationOfZero.col == 0) {
            // swap tile(0, this.dimension -2) with tile (0, this.dimension -1)
            temp = newTiles[0][this.dimension - 2];
            newTiles[0][this.dimension - 2] = newTiles[0][this.dimension - 1];
            newTiles[0][this.dimension - 1] = temp;
        } else {
            // zero is not on the left edge.
            // swap tile(0, 0) with tile (0, 1)
            // this is the same as in condition above. Will be refactored to avoid code
            // duplication.
            temp = newTiles[0][1];
            newTiles[0][1] = newTiles[0][0];
            newTiles[0][0] = temp;
        }

        twinBoard = new Board(newTiles);
        return (twinBoard);

    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // intentionally left empty
    }

}
