public class Board {

    private int[][] tiles;
    private final int dimension;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("Board is not initialized properly.");
        }
        this.tiles = tiles.clone();
        this.dimension = this.tiles.length;
    }

    // string representation of this board
    public String toString() {
        return new String("");
    }

    // board dimension n
    public int dimension() {
        return this.dimension;
    }

    // number of tiles out of place
    public int hamming() {
        // expected tile value: (row*dimension) + (col) + 1
        int hammingDistance = 0;
        int expectedTileValue = 0;
        for (int row = 0; row < this.dimension; ++row) {
            for (int col = 0; col < this.dimension; ++col) {
                if (this.tiles[row][col] == 0) {
                    continue;
                }
                expectedTileValue = (row * this.dimension) + (col) + 1;
                hammingDistance = hammingDistance + ((expectedTileValue != this.tiles[row][col]) ? 1 : 0);
            }
        }
        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return 0;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return false;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        return false;
    }

    // // all neighboring boards
    // public Iterable<Board> neighbors() {

    // }

    // // a board that is obtained by exchanging any pair of tiles
    // public Board twin() {

    // }

    // unit testing (not graded)
    public static void main(String[] args) {

    }

}
