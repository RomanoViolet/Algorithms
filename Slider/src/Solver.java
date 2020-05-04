import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;

public class Solver {
    private boolean solved = false;
    private int totalSteps = 0;
    private Board startingBoard;
    private SearchableMinPQ list = new SearchableMinPQ();
    private SearchableMinPQ twinList = new SearchableMinPQ();
    // private Bag<ComparableBoard> closedList;
    // private SET<ComparableBoard> closedList = new SET<ComparableBoard>();
    private Stack<Board> trace = new Stack<Board>();

    // TODO Searchable OpenList which will:
    // A. provide the lowest cost board with min
    // B. answer whether an object is already in stored.
    // TODO: Create a generic SearchablePQ
    private class SearchableMinPQ {
        private MinPQ<ComparableBoard> priorityQueueList = new MinPQ<ComparableBoard>();
        private SET<String> searcheableOpenList = new SET<String>();
        private SET<String> closedList = new SET<String>();

        public void insert(ComparableBoard board) {
            // insert only in the open list
            this.priorityQueueList.insert(board);
            this.searcheableOpenList.add(board.thisBoard.toString());
            // System.out.println("Elements in Open List: " +
            // this.priorityQueueList.size());
        }

        public boolean contains(ComparableBoard board) {
            return (this.searcheableOpenList.contains(board.thisBoard.toString()));
        }

        // return the minimum key and remove it from the storage
        public ComparableBoard extractMin() {

            // TODO Remove after debugging
            // System.out.printf("%s", "Available Node Costs:");
            // for (ComparableBoard b : this.priorityQueueList) {
            // System.out.printf("%d ", b.getFCost());
            // }
            // System.out.println("");

            ComparableBoard result = this.priorityQueueList.min();
            this.priorityQueueList.delMin();
            this.searcheableOpenList.delete(result.thisBoard.toString());
            return (result);
        }

        public boolean isEmpty() {
            assert (this.priorityQueueList.isEmpty() == this.searcheableOpenList
                    .isEmpty()) : "Search and Priority lists do not have identical contents";
            return (this.priorityQueueList.isEmpty());

        }

        public void addToClosedNodes(ComparableBoard board) {
            this.closedList.add(board.thisBoard.toString());

            // TODO Remove after debugging
            // System.out.println("Elements in Closed List: " + this.closedList.size());
        }

        public boolean closedListContains(ComparableBoard board) {
            return (this.closedList.contains(board.thisBoard.toString()));
        }

    }

    // the ComparableBoard class, required for MinPQ to work.
    // wraps the Board datatype to have cost function and pointer to parent.
    private class ComparableBoard implements Comparable<ComparableBoard> {
        // costs
        int f = Integer.MAX_VALUE;
        int g = 0;
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
            this.updateHCost();
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
        while (currentBoard != null && currentBoard.thisBoard != null) {
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
        this.list.insert(startBoard);

        // twin
        Board twin = this.startingBoard.twin();

        // TODO Remove after debugging
        // System.out.println("Original Board:\n" + this.startingBoard.toString());
        // System.out.println("Twin Board:\n" + twin.toString());

        ComparableBoard twinBoard = new ComparableBoard(twin);
        twinBoard.updateGCost(0);

        // insert the twin board into the openlist
        this.twinList.insert(twinBoard);

        boolean solved = false;
        boolean twinSolved = false;
        while (!solved && !twinSolved) {
            solved = solve(this.list);
            twinSolved = solve(this.twinList);
        }

        this.solved = solved;

    }

    private boolean solve(SearchableMinPQ list) {
        // exploration
        if (!list.isEmpty()) {

            // pick out the cheapest from the open list
            ComparableBoard current = list.extractMin();

            // put the current to closed list
            list.addToClosedNodes(current);

            // check for termination
            if (current.thisBoard.manhattan() == 0) {
                // solved the puzzle.
                // this.solved = true;
                this.totalSteps = current.getGCost();
                this.trace = traceAllBoards(current);
                return (true);
            }

            // TODO Remove all debugging
            // System.out.println("Current: \n" + current.thisBoard.toString());
            // System.out.println("Manhattan: " + current.thisBoard.manhattan());

            ComparableBoard thisNeighbor;
            for (Board b : current.thisBoard.neighbors()) {
                // for each of the neighbors
                thisNeighbor = new ComparableBoard(b, current.thisBoard);

                // is thisNeighbor in closedList?
                if (list.closedListContains(thisNeighbor)) {
                    // skip this neighbor
                    // assume consistent and valid cost function.

                    // TODO: Remove after debugging
                    // System.out.println("Neighbor closed:\n" + thisNeighbor.thisBoard.toString());
                    // System.out.println("Neighbor closed");

                    continue;
                }

                // compute the new g function for thisNeighbor
                // A transition to a neihboring board adds 1 to the g-cost
                int newGScore = current.getGCost() + 1;

                // f-cost is updated when updateGCost is called.

                // Is thisNeighbor in the openList already?
                if (list.contains(thisNeighbor) && (thisNeighbor.getGCost() > newGScore)) {
                    // Found a cheaper route to this neighbor
                    thisNeighbor.updateGCost(newGScore);
                } else {
                    // add it to the open list
                    thisNeighbor.updateGCost(newGScore);
                    thisNeighbor.previousBoard = current;
                    list.insert(thisNeighbor);
                }

                // TODO: Remove after debugging
                // System.out.println("---\nNeighbor:\n" + thisNeighbor.thisBoard.toString());
                // System.out.println("Cost of Neighbor:\n" + thisNeighbor.getFCost() +
                // "\n---\n");

                // thisNeighbor.updateGCost();

            }
        }
        return (false);
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
