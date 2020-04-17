import edu.princeton.cs.algs4.StdRandom;
import java.util.NoSuchElementException;

public class RandomizedQueue<T> implements Iterable<T> {

    private T[] internalArray;
    private final int initialSizeOfInteranArray = 1;
    private int numberOfElementsInArray;

    // construct an empty randomized queue
    public RandomizedQueue() {
        // resizing arrays are used.

        // cast an Object array to the required type.
        // throws an unavoidable warning which is suppressed.
        @SuppressWarnings("unchecked")
        this.internalArray = (T[]) new Object[initialSizeOfInteranArray];
        this.numberOfElementsInArray = 0;
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
        @SuppressWarnings("unchecked")
        T[] largerArray = (T[]) new Object[2 * this.internalArray.length];
        for (int i = 0; i < this.internalArray.length; ++i) {
            largerArray[i] = this.internalArray[i];
        }

        this.internalArray = largerArray;
        largerArray = null;

    }

    // add the item
    public void enqueue(T item) {
        // is array too full?
        if (this.numberOfElementsInArray == this.internalArray.length) {
            this.doubleTheSizeOfArray();
        }
        this.internalArray[this.numberOfElementsInArray++] = item;
    }

    // remove and return a random item
    public T dequeue() {

        // is the array empty of elements?
        if (this.numberOfElementsInArray == 0)
        {
            throw new NoSuchElementException("No elements to dequeue.");
        }

        // Choose the location to remove item from
        int siteTobeUnblocked = StdRandom.uniform(this.numberOfElementsInArray);

        // get the item
        T result = this.internalArray[siteTobeUnblocked];

        // switch contents at location siteTobeUnblocked with the end of the array
        // (this.numberOfElementsInArray). That is, bubble the gap due to withdrawn
        // element to the top of the array
        this.internalArray[siteTobeUnblocked] = this.internalArray[this.numberOfElementsInArray];

        // now mark the top of the array as empty. Let garbage collector do its job
        this.internalArray[this.numberOfElementsInArray] = null;

        // decrement the number of elements
        this.numberOfElementsInArray--;

        // if the array got too empty, halve the size of the array
        // Since the initial array length is 1, and it is always doubled, division by 4 is exact only after 2 doublings.
        //
        // Corner Test Case
        // A. Array of size 1 --> Queue (elements: 1) --> Double (size = 2) --> Dequeue (elements: 0, length = 2)
        //    For this case, the halving condition (integer division: (elments = 0) ==   (2/4 = 0) is met.
        //    However, enqueing 1 element will lead to array doubling, and subsequent dequeing 1 element will lead to array halving.
        //    (Specifically, array capacity thrashing occurs.)
        if (this.numberOfElementsInArray <= this.internalArray.length / 4)

    }

    // return a random item (but do not remove it)
    public T sample() {

    }

    // return an independent iterator over items in random order
    public Iterator<T> iterator() {

    }

    // unit testing (required)
    public static void main(String[] args) {

    }

}
