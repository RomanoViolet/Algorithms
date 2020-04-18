import edu.princeton.cs.algs4.StdRandom;
import java.util.NoSuchElementException;
import java.util.Iterator;

public class RandomizedQueue<T> implements Iterable<T> {

    private static final int INITIAL_SIZE_OF_INTERNAL_ARRAY = 1;
    private T[] internalArray;
    private int numberOfElementsInArray;

    // construct an empty randomized queue
    public RandomizedQueue() {
        // resizing arrays are used.

        // cast an Object array to the required type.
        // throws an unavoidable warning which is suppressed.
        //
        // Princeton does not allow suppression of this warning. Dammit.
        // @SuppressWarnings("unchecked")
        this.internalArray = (T[]) new Object[INITIAL_SIZE_OF_INTERNAL_ARRAY];
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
        T[] largerArray = (T[]) new Object[2 * this.internalArray.length];
        for (int i = 0; i < this.internalArray.length; ++i) {
            largerArray[i] = this.internalArray[i];
        }

        this.internalArray = largerArray;

        // see https://stackoverflow.com/a/18109981
        // largerArray = null;

    }

    // add the item
    public void enqueue(T item) {
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
        T[] smallerArray = (T[]) new Object[this.internalArray.length / 2];
        for (int i = 0; i < this.internalArray.length; ++i) {
            smallerArray[i] = this.internalArray[i];
        }

        this.internalArray = smallerArray;

        // see https://stackoverflow.com/a/18109981
        // smallerArray = null;
    }

    // remove and return a random item
    public T dequeue() {

        // is the array empty of elements?
        if (this.numberOfElementsInArray == 0) {
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
    public T sample() {
        // is the array empty of elements?
        if (this.numberOfElementsInArray == 0) {
            throw new NoSuchElementException("No elements to sample.");
        }

        // Choose the location to remove item from
        int siteTobeUnblocked = StdRandom.uniform(this.numberOfElementsInArray);

        // get the item
        T result = this.internalArray[siteTobeUnblocked];

        return (result);
    }

    // return an independent iterator over items in random order
    public Iterator<T> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<T> {
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
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No element remaining.");
            }

            for (int i = 0; i < temporaryArray.length; ++i) {
                // content is the index of the element
                temporaryArray[i] = i;
            }

            // shuffle the array
            StdRandom.shuffle(temporaryArray);

            T result = RandomizedQueue.this.internalArray[currentIndex];
            currentIndex++;
            return (result);
        }

    }

    // unit testing (required)
    public static void main(String[] args) {

    }

}
