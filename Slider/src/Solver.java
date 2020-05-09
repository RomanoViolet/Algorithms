import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.BinarySearchST;
import edu.princeton.cs.algs4.MinPQ;

public class Solver {

    private Stack<Board> trace = new Stack<Board>();
    private SearchableMinPQ mainList = new SearchableMinPQ();
    private SearchableMinPQ twinList = new SearchableMinPQ();
    private Node mainBoard;
    private Node twinBoard;
    private int totalBoardExamined;
    private boolean isSolvable;
    private int numberOfMoves;

    private class SearchableMinPQ {
        private MinPQ<Node> openList = new MinPQ<Node>();
        private BinarySearchST<String, Integer> searchableOpenList = new BinarySearchST<String, Integer>();
        private BinarySearchST<String, Integer> closedList = new BinarySearchST<String, Integer>();

        public void insert(Node node) {
            this.openList.insert(node);
            this.searchableOpenList.put(node.board.toString(), 0);
        }

        public boolean contains(Node node) {
            return (this.searchableOpenList.contains(node.board.toString()));
        }

        // return the minimum key and remove it from the storage
        public Node extractMin() {

            Node result = this.openList.min();
            this.openList.delMin();
            this.searchableOpenList.delete(result.board.toString());
            return (result);
        }

        public boolean isEmpty() {
            assert (this.openList.isEmpty() == this.searchableOpenList
                    .isEmpty()) : "Search and Priority lists do not have identical contents";
            return (this.openList.isEmpty());

        }

        public void addToClosedNodes(Node node) {
            this.closedList.put(node.board.toString(), 0);
        }

        public boolean closedListContains(Node node) {
            return (this.closedList.contains(node.board.toString()));
        }
    }

    private class Node implements Comparable<Node> {
        Board board;
        int f = Integer.MAX_VALUE;
        int g = Integer.MAX_VALUE;
        int h = 0;
        Node previousBoard;

        public Node(Board thisBoard) {
            this.board = thisBoard;
            this.previousBoard = null;
            this.updateHCost();
        }

        public Node(Board thisBoard, Board previousBoard) {
            this.board = thisBoard;
            this.previousBoard = new Node(previousBoard);
            this.updateHCost();
        }

        public void updateGCost(int gCost) {
            this.g = gCost;

            // all information to compute f is available now.
            this.f = this.g + this.h;
        }

        public void updateHCost() {
            this.h = this.board.manhattan();
        }

        public int getGCost() {
            return (this.g);
        }

        public int getFCost() {
            return (this.f);
        }

        public int compareTo(Node that) {
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

    private Stack<Board> traceAllBoards(Node current) {
        Stack<Board> localTrace = new Stack<Board>();
        Node currentBoard = current;
        while (currentBoard != null && currentBoard.board != null) {
            localTrace.push(currentBoard.board);
            currentBoard = currentBoard.previousBoard;
        }

        return localTrace;

    }

    private class SolverState {
        Node node;
        int totalBoardsExamined;
        SearchableMinPQ list;
    }

    public Solver(Board initial) {

        if (initial == null) {
            throw new IllegalArgumentException("No initial board provided for the solver");
        }

        this.mainBoard = new Node(initial);
        this.twinBoard = new Node(initial.twin());

        mainBoard.updateGCost(0);
        twinBoard.updateGCost(0);

        this.mainList.insert(mainBoard);
        this.twinList.insert(twinBoard);

        SolverState solverStateMainBoard = new SolverState();
        solverStateMainBoard.node = this.mainBoard;
        solverStateMainBoard.totalBoardsExamined = 0;
        solverStateMainBoard.list = this.mainList;

        SolverState solverStateTwinBoard = new SolverState();
        solverStateTwinBoard.node = this.twinBoard;
        solverStateTwinBoard.totalBoardsExamined = 0;
        solverStateTwinBoard.list = this.twinList;

        while (!solverStateMainBoard.node.board.isGoal() && !solverStateTwinBoard.node.board.isGoal()
                && this.totalBoardExamined < 50) {
            solverStateMainBoard = solve(solverStateMainBoard);
            solverStateTwinBoard = solve(solverStateTwinBoard);

        }

        if (solverStateMainBoard.node.board.isGoal()) {
            this.isSolvable = true;
            this.numberOfMoves = this.trace.size() - 1;
        } else {
            this.isSolvable = false;
            this.numberOfMoves = -1;
        }

    }

    private SolverState solve(SolverState state) {
        SearchableMinPQ list = state.list;
        Node node = state.node;
        int progressCounter = state.totalBoardsExamined;
        if (!list.isEmpty()) {
            Node cheapestNode = list.extractMin();
            list.addToClosedNodes(cheapestNode);

            if (cheapestNode.board.isGoal()) {
                this.trace = traceAllBoards(cheapestNode);
                // this.numberOfMoves = cheapestNode.getGCost();
                node = cheapestNode;

                SolverState returnSolverState = new SolverState();
                returnSolverState.list = list;
                returnSolverState.node = cheapestNode;
                returnSolverState.totalBoardsExamined = progressCounter;

                return returnSolverState;
            }

            progressCounter++;

            for (Board thisNeighbor : cheapestNode.board.neighbors()) {

                Node newNode = new Node(thisNeighbor, cheapestNode.board);
                if (list.closedListContains(newNode)) {
                    continue;
                }

                int newGScore = newNode.getGCost() + 1;
                // f-cost is updated when updateGCost is called.

                if (list.contains(newNode) && (newGScore < newNode.getGCost())) {
                    // Found a cheaper route to this neighbor
                    newNode.updateGCost(newGScore);
                } else if (!list.contains(newNode)) {
                    newNode.updateGCost(newGScore);
                    newNode.previousBoard = cheapestNode;
                    list.insert(newNode);

                } else {
                    // skip
                }
            }
        }
        SolverState returnSolverState = new SolverState();
        returnSolverState.list = list;
        returnSolverState.node = state.node;
        returnSolverState.totalBoardsExamined = progressCounter;
        return (returnSolverState);
    }

    public boolean isSolvable() {
        return isSolvable;
    }

    public int moves() {
        // if (this.isSolvable) {
        // return (this.numberOfMoves);
        // } else {
        // return -1;
        // }
        return (this.numberOfMoves);
    }

    public Iterable<Board> solution() {
        if (this.isSolvable) {
            return (this.trace);
        } else {
            return null;
        }

    }

    public static void main(String[] args) {
        // intentionally left blank

    }
}
