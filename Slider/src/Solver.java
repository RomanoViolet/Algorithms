import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;

public class Solver {
    private boolean solved = false;
    private int totalSteps = 0;
    private Board startingBoard;
    private SearchableMinPQ openList;
    // private Bag<ComparableBoard> closedList;
    private SET<ComparableBoard> closedList;
    private Stack<Board> trace;

    // TODO Searchable OpenList which will:
    // A. provide the lowest cost board with min
    // B. answer whether an object is already in stored.
    // TODO: Create a generic SearchablePQ
    private class SearchableMinPQ {
        private MinPQ<ComparableBoard> priorityQueueList;
        private SET<ComparableBoard> searchList;

        public void insert(ComparableBoard board) {
            this.priorityQueueList.insert(board);
            this.searchList.add(board);
        }

        public boolean contains(ComparableBoard board) {
            return (this.searchList.contains(board));
        }

        // return the minimum key and remove it from the storage
        public ComparableBoard extractMin() {
            ComparableBoard result = this.priorityQueueList.min();
            this.priorityQueueList.delMin();
            this.searchList.delete(result);
            return (result);
        }

        public boolean isEmpty() {
            assert (this.priorityQueueList.isEmpty() == this.searchList
                    .isEmpty()) : "Search and Priority lists do not have identical contents";
            return (this.priorityQueueList.isEmpty());

        }

    }

    // the ComparableBoard class, required for MinPQ to work.
    // wraps the Board datatype to have cost function and pointer to parent.
    private class ComparableBoard implements Comparable<ComparableBoard> {
        // costs
        int f = 0;
        int g = Integer.MAX_VALUE;
        int h = 0;
        private Board thisBoard;
        private ComparableBoard previousBoard;

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
            this.previousBoard = new ComparableBoard(previousBoard);
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

    private Stack<Board> traceAllBoards(ComparableBoard current) {
        Stack<Board> trace = new Stack<Board>();
        ComparableBoard currentBoard = current;
        while (currentBoard.thisBoard != null) {
            trace.push(currentBoard.thisBoard);
            currentBoard = currentBoard.previousBoard;
        }

        return trace;

    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("No initial board provided for the solver");
        }
        this.startingBoard = initial;
        this.solved = false;

        // wrap the board into ComparableBoard
        ComparableBoard startBoard = new ComparableBoard(this.startingBoard);
        startBoard.updateGCost(0);

        // insert the starting board into the openlist
        this.openList.insert(startBoard);

        // exploration
        while (!this.openList.isEmpty()) {
            // pick out the cheapest from the open list
            ComparableBoard current = this.openList.extractMin();

            // put the current to closed list
            this.closedList.add(current);

            // check for termination
            if (current.thisBoard.manhattan() == 0) {
                // solved the puzzle.
                this.solved = true;
                this.totalSteps = current.getGCost();
                this.trace = traceAllBoards(current);
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
                if (this.openList.contains(thisNeighbor) && (thisNeighbor.getGCost() > newGScore)) {
                    // Found a cheaper route to this neighbor
                    thisNeighbor.updateGCost(newGScore);
                } else {
                    // add it to the open list
                    this.openList.insert(thisNeighbor);
                    thisNeighbor.previousBoard = current;
                }

                // thisNeighbor.updateGCost();

            }
        }

    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return (this.solved);
    }

    // min number of moves to solve initial board
    public int moves() {
        return (this.totalSteps);
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return (this.trace);
    }

    // test client (see below)
    public static void main(String[] args) {

    }
}
