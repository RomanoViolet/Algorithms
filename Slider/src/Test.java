import edu.princeton.cs.algs4.StdIn;

public class Test {
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

        Board b = new Board(tiles);
        System.out.println("Hamming: " + b.hamming());

    }

}
