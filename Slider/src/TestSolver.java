import edu.princeton.cs.algs4.StdIn;

public class TestSolver {
    public static void main(String[] args) {
        String in = StdIn.readString();
        int n = Integer.parseInt(in);
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String tileValue = StdIn.readString();
                tiles[i][j] = Integer.parseInt(tileValue);
            }
        }
        Board board = new Board(tiles);
        Solver s = new Solver(board);
        for (Board b : s.solution()) {
            b.toString();
        }

    }
}
