import edu.princeton.cs.algs4.StdRandom;
import java.util.NoSuchElementException;
import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private static final int INITIAL_SIZE_OF_INTERNAL_ARRAY = 1;
    private Item[] internalArray;
    private int numberOfElementsInArray;

    // construct an empty randomized queue
    public RandomizedQueue() {
        // resizing arrays are used.

        // cast an Object array to the required type.
        // throws an unavoidable warning which is suppressed.
        //
        // Princeton does not allow suppression of this warning. Dammit.
        // @SuppressWarnings("unchecked")
        this.internalArray = (Item[]) new Object[INITIAL_SIZE_OF_INTERNAL_ARRAY];
        numberOfElementsInArray = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return (this.numberOfElementsInArray == 0);
    }

    // return the number of items on the randomized queue
    public int size() {
        return (this.numberOfElementsInArray);
    }

    private void doubleTheSizeOfArray() {
        // double the size of the Array

        // Princeton does not allow suppression of this warning. Dammit.
        // @SuppressWarnings("unchecked")
        Item[] largerArray = (Item[]) new Object[2 * this.internalArray.length];
        for (int i = 0; i < this.internalArray.length; ++i) {
            largerArray[i] = this.internalArray[i];
        }

        this.internalArray = largerArray;

        // see https://stackoverflow.com/a/18109981
        // largerArray = null;

    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("An argument is required");
        }

        // is array too full?
        if (this.numberOfElementsInArray == this.internalArray.length) {
            this.doubleTheSizeOfArray();
        }
        this.internalArray[this.numberOfElementsInArray++] = item;
    }

    private void halfTheSizeOfArray() {

        // Princeton does not allow suppression of this warning. Dammit.
        // @SuppressWarnings("unchecked")
        Item[] smallerArray = (Item[]) new Object[this.internalArray.length / 2];
        for (int i = 0; i < this.numberOfElementsInArray; ++i) {
            smallerArray[i] = this.internalArray[i];
        }

        this.internalArray = smallerArray;

        // see https://stackoverflow.com/a/18109981
        // smallerArray = null;
    }

    // remove and return a random item
    public Item dequeue() {

        // is the array empty of elements?
        if (this.numberOfElementsInArray == 0) {
            throw new NoSuchElementException("No elements to dequeue.");
        }

        // Choose the location to remove item from
        int siteTobeUnblocked = StdRandom.uniform(this.numberOfElementsInArray);

        // get the item
        Item result = this.internalArray[siteTobeUnblocked];

        // switch contents at location siteTobeUnblocked with the end of the array
        // (this.numberOfElementsInArray). That is, bubble the gap due to withdrawn
        // element to the top of the array
        this.internalArray[siteTobeUnblocked] = this.internalArray[this.numberOfElementsInArray - 1];

        // now mark the top of the array as empty. Let garbage collector do its job
        this.internalArray[this.numberOfElementsInArray - 1] = null;

        // decrement the number of elements
        this.numberOfElementsInArray--;

        // if the array got too empty, halve the size of the array
        // Since the initial array length is 1, and it is always doubled, division by 4
        // is exact only after 2 doublings.
        //
        // Corner Test Case
        // A. Array of size 1 --> Queue (elements: 1) --> Double (size = 2) --> Dequeue
        // (elements: 0, length = 2)
        // For this case, the halving condition (integer division: (elments = 0) == (2/4
        // = 0) is met.
        // However, enqueing 1 element will lead to array doubling, and subsequent
        // dequeing 1 element will lead to array halving.
        // Specifically, array capacity thrashing occurs. Therefore, we do not halve the
        // array unless its length is minimum 4.
        if (this.internalArray.length >= 4 && (this.numberOfElementsInArray <= this.internalArray.length / 4)) {
            this.halfTheSizeOfArray();
        }

        return (result);

    }

    // return a random item (but do not remove it)
    public Item sample() {
        // is the array empty of elements?
        if (this.numberOfElementsInArray == 0) {
            throw new NoSuchElementException("No elements to sample.");
        }

        // Choose the location to remove item from
        int siteTobeUnblocked = StdRandom.uniform(this.numberOfElementsInArray);

        // get the item
        Item result = this.internalArray[siteTobeUnblocked];

        return (result);
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        // https://stackoverflow.com/a/1816462 for accessing member of the parent class

        // an array which is as long as number of elements stored
        int[] temporaryArray = new int[RandomizedQueue.this.numberOfElementsInArray];
        int currentIndex = 0;

        public boolean hasNext() {
            return (currentIndex != temporaryArray.length);
        }

        // method not supported
        public void remove() {
            throw new UnsupportedOperationException();
        }

        // the actual position of the head must not change.
        // only a removeFirst operation changes the actual position of the head.
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No element remaining.");
            }

            // randomization is done only once per iterator creation
            if (currentIndex == 0) {
                for (int i = 0; i < temporaryArray.length; ++i) {
                    // content is the index of the element
                    temporaryArray[i] = i;
                }

                // shuffle the array
                StdRandom.shuffle(temporaryArray);
            }

            Item result = RandomizedQueue.this.internalArray[temporaryArray[currentIndex]];
            // System.out.println("Returning: " + result);
            currentIndex++;
            return (result);
        }

    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> r = new RandomizedQueue<>();

        // Evaluation of the logical expression to false triggers the assert.
        assert r.isEmpty() : "Randomized Queue is not empty as expected";

        r.enqueue(0);
        assert r.size() == 1 : "Incorrect number of elements in the randomizedQueue";

        assert r.sample() == 0 : "Content of the randomizedQueue is incorrect";

        for (int i : r) {
            assert i == 0 : "Content of the randomizedQueue is incorrect";
        }

        r.enqueue(1);
        r.enqueue(2);
        r.enqueue(3);
        System.out.println("The following two lines should list the contents in a different order");

        for (int i : r) {
            System.out.printf("%d, ", i);
        }
        System.out.println();
        for (int i : r) {
            System.out.printf("%d, ", i);
        }
        System.out.println();

        System.out.printf("DEQUEUE: %d, %d, %d, %d", r.dequeue(), r.dequeue(), r.dequeue(), r.dequeue());
        System.out.println();
    }

}
