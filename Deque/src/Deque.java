import java.util.NoSuchElementException;
import java.util.Iterator;

//
// pronounced "deck"
// exercise: https://www.coursera.org/learn/algorithms-part1/programming/zamjZ/deques-and-randomized-queues
//
public class Deque<T> implements Iterable<T> {

    // helper linked list class
    private class Node {
        private T item = null;
        private Node next = null;
    }

    private Node head = null;
    private Node tail = null;
    private int size = 0;

    // construct an empty deque
    public Deque() {
        // head and tail are the same node
        head = tail;
        // size of the deque.
        size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return (this.size == 0);
    }

    // return the number of items on the deque
    public int size() {
        return (this.size);
    }

    // add the item to the front
    public void addFirst(T item) {

        if (item == null) {
            throw new IllegalArgumentException("An argument is required");
        }
        // new item becomes the new head
        Node newHead = new Node();
        newHead.item = item;

        // linkage: A <-- B <-- C <-- head <-- newHead
        // newhead.next should point to (old)head
        newHead.next = this.head;

        // mark the new head.
        this.head = newHead;

        // if this the first item starting from an empty deque, then tail also points to
        // the head as head and tail are now valid nodes.
        if (this.size == 0) {
            this.tail = this.head;
        }

        // increment size.
        this.size++;
    }

    // add the item to the back
    public void addLast(T item) {

        if (item == null) {
            throw new IllegalArgumentException("An argument is required");
        }
        // new item becomes the new tail (or extends the tail)
        Node newTail = new Node();
        newTail.item = item;

        // linkage: newTail --> (old)Tail --> A --> B -->C ...
        newTail.next = this.tail;

        // mark the new tail
        this.tail = newTail;

        // increment the size
        this.size++;

    }

    // remove and return the item from the front
    public T removeFirst() {

        if (isEmpty()) {
            throw new NoSuchElementException("Deque underflow");
        }

        // Extract the value of the head.
        T returnValue = this.head.item;

        // Move the head back
        this.head = this.head.next;

        // decrement the size
        this.size--;

        // return the value
        return (returnValue);

    }

    // remove and return the item from the back
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque underflow");
        }

        // Extract the value of the head.
        T returnValue = this.tail.item;

        // decrement the size
        this.size--;

        // Move the tail back
        // if the deque had only 1 item, then both head and tail now point to null
        if (this.size == 0) {
            this.head = null;
            this.tail = null;
        } else {
            // deque had a minimum of 2 elements before the current decrement.
            this.tail = this.tail.next;
        }

        return (returnValue);

    }

    // return an iterator over items in order from front to back
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        // see https://stackoverflow.com/a/1816462 for accessing member of the parent
        // class
        private Node currentNode = Deque.this.head;

        public boolean hasNext() {
            return (currentNode != null);
        }

        // method not supported
        public void remove() {
            throw new UnsupportedOperationException();
        }

        // the actual position of the head must not change.
        // only a removeFirst operation changes the actual position of the head.
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T result = currentNode.item;
            currentNode = currentNode.next;
            return (result);
        }

    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> d = new Deque<>();

        // assert is thrown when the condition evaluates to false.
        assert d.isEmpty() : "Error: Deque not empty.";

        assert d.size() == 0 : "Error: Size of the deque is not 0";

        // insert the first item
        d.addFirst(4);

        assert d.size() == 1 : "Error: Increment of size incorrect";
        assert d.isEmpty() == false : "Error: Deque should not be empty";

        d.addFirst(5);
        d.addFirst(6);

        assert d.size() == 3 : "Error: Increment of size incorrect";

    }

}
