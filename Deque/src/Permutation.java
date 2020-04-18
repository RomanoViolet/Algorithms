import edu.princeton.cs.algs4.StdIn;

public class Permutation {
    public static void main(String[] args) {
        int nElementstoPrint = Integer.parseInt(args[0]);
        RandomizedQueue<String> r = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            r.enqueue(item);
        }

        // print out elements
        for (int i = 0; i < nElementstoPrint; ++i) {
            System.out.println(r.dequeue());
        }
    }
}
