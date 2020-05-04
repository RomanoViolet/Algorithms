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
        if (!s.isSolvable())
            System.out.println("No solution possible");
        else {
            for (Board b : s.solution()) {
                System.out.println(b.toString());
            }
            System.out.println("Solver required " + s.moves() + " moves.");
        }
    }
}
