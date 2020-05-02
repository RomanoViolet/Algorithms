import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.SET;

public class Solver {
    private Board startingBoard;
    private MinPQ<ComparableBoard> openList;
    // private Bag<ComparableBoard> closedList;
    private SET<ComparableBoard> closedList;

    // TODO Searchable OpenList which will:
    // A. provide the lowest cost board with min
    // B. answer whether an object is already in stored.
    private class SearchableMinPQ {

    }

    // the ComparableBoard class, required for MinPQ to work.
    // wraps the Board datatype to have cost function and pointer to parent.
    private class ComparableBoard implements Comparable<ComparableBoard> {
        // costs
        int f = 0;
        int g = Integer.MAX_VALUE;
        int h = 0;
        private Board thisBoard;
        private Board previousBoard;

        public void updateGCost(int gCost) {
            this.g = gCost;

            // all information to compute f is available now.
            this.f = this.g + this.h;
        }

        public void updateHCost() {
            this.h = this.thisBoard.manhattan();
        }

        public int getGCost() {
            return (this.g);
        }

        public ComparableBoard(Board thisBoard, Board previousBoard) {
            this.thisBoard = thisBoard;
            this.previousBoard = previousBoard;
            this.updateHCost();

        }

        public ComparableBoard(Board thisBoard) {
            this.thisBoard = thisBoard;
            this.previousBoard = null;
        }

        public int getFCost() {
            return (this.f);
        }

        public int compareTo(ComparableBoard that) {
            // compare to manhattan distance based metric
            if (this.getFCost() < that.getFCost()) {
                return (-1);
            }
            if (this.getFCost() > that.getFCost()) {
                return (1);
            }
            return (0);

        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        this.startingBoard = initial;

        // wrap the board into ComparableBoard
        ComparableBoard startBoard = new ComparableBoard(this.startingBoard);
        startBoard.updateGCost(0);

        // insert the starting board into the openlist
        this.openList.insert(startBoard);

        // exploration
        while (!this.openList.isEmpty()) {
            // pick out the cheapest from the open list
            ComparableBoard current = this.openList.min();

            // put the current to closed list
            this.closedList.add(current);
            this.openList.delMin();

            // check for termination
            if (current.thisBoard.manhattan() == 0) {
                // solved the puzzle.
                break;
            }

            ComparableBoard thisNeighbor;
            for (Board b : current.thisBoard.neighbors()) {
                // for each of the neighbors
                thisNeighbor = new ComparableBoard(b, current.thisBoard);

                // is thisNeighbor in closedList?
                if (this.closedList.contains(thisNeighbor)) {
                    // skip this neighbor
                    // assume consistent and valid cost function.
                    continue;
                }

                // compute the new g function for thisNeighbor
                // A transition to a neihboring board adds 1 to the g-cost
                int newGScore = current.getGCost() + 1;

                // f-cost is updated when updateGCost is called.

                // Is thisNeighbor in the openList already?
                if (this.openList.

                // thisNeighbor.updateGCost();

            }
        }

    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {

    }

    // min number of moves to solve initial board
    public int moves() {

    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {

    }

    // test client (see below)
    public static void main(String[] args) {

    }
}
