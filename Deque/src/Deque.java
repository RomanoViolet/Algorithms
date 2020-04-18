import java.util.NoSuchElementException;
import java.util.Iterator;

//
// pronounced "deck"
// exercise: https://www.coursera.org/learn/algorithms-part1/programming/zamjZ/deques-and-randomized-queues
//
public class Deque<Item> implements Iterable<Item> {

    // helper linked list class
    private class Node {
        private Item item = null;
        private Node nextTowardsTail = null;
        private Node nextTowardsHead = null;
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
    public void addFirst(Item item) {

        if (item == null) {
            throw new IllegalArgumentException("An argument is required");
        }
        // new item becomes the new head
        Node newHead = new Node();
        newHead.item = item;

        // linkage: tail <-- A <-- B <-- C <-- (old)head <-- newHead
        // newhead.next should point to (old)head
        // direction of links: from head towards tail.
        newHead.nextTowardsTail = this.head;

        // mark the new head.
        this.head = newHead;

        // if this the first item starting from an empty deque, then tail also points to
        // the head as head and tail are now valid nodes.
        if (this.size == 0) {
            this.tail = this.head;
            // next element are still null
        }

        // increment size.
        this.size++;

        // if we have now two elements, next elements can have values
        // direction of links: from tail towards head.
        // tail --> head
        if (this.size >= 2) {
            this.head.nextTowardsTail.nextTowardsHead = this.head;
        }
    }

    // add the item to the back
    public void addLast(Item item) {

        if (item == null) {
            throw new IllegalArgumentException("An argument is required");
        }
        // new item becomes the new tail (or extends the tail)
        Node newTail = new Node();
        newTail.item = item;

        // linkage: newTail --> (old)Tail --> A --> B -->C ...
        newTail.nextTowardsHead = this.tail;

        // mark the new tail
        this.tail = newTail;

        // increment the size
        this.size++;

        // if this is the only element in the dequeue, the head and tail are the same
        if (this.size == 1) {
            this.head = this.tail;
            // links remain null;
        } else {
            // make head-to-tail link as well from Tail <-- A (old tail)
            this.tail.nextTowardsHead.nextTowardsTail = this.tail;
        }

    }

    // remove and return the item from the front
    public Item removeFirst() {

        if (isEmpty()) {
            throw new NoSuchElementException("Deque underflow");
        }

        // Extract the value of the head.
        Item returnValue = this.head.item;

        // Move the head back
        this.head = this.head.nextTowardsTail;

        // there is no head to point to from the new head
        if (this.head != null) {
            this.head.nextTowardsHead = null;
        }

        // decrement the size
        this.size--;

        // if this remove step empties the deque
        if (this.size == 0) {
            this.head = null;
            this.tail = null;
        }

        // return the value
        return (returnValue);

    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque underflow");
        }

        // Extract the value of the head.
        Item returnValue = this.tail.item;

        // decrement the size
        this.size--;

        // Move the tail back
        // if the deque had only 1 item, then both head and tail now point to null
        if (this.size == 0) {
            this.head = null;
            this.tail = null;
        } else {
            // deque had a minimum of 2 elements before the current decrement.
            this.tail = this.tail.nextTowardsHead;

            // there is no tail to point towards from the new tail
            this.tail.nextTowardsTail = null;
        }

        return (returnValue);

    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
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
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Item result = currentNode.item;
            currentNode = currentNode.nextTowardsTail;
            return (result);
        }

    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> d = new Deque<>();

        // assert is thrown when the condition evaluates to false.
        assert d.isEmpty() : "Error: Deque not empty.";

        assert d.size() == 0 : "Error: Size of the deque is not 0";

        d.addLast(-1);

        // insert the first item
        d.addFirst(4);

        assert d.size() == 2 : "Error: Increment of size incorrect";
        assert !d.isEmpty() : "Error: Deque should not be empty";

        d.addFirst(5);
        d.addFirst(6);
        d.addLast(-2);

        assert d.size() == 5 : "Error: Increment of size incorrect";

        int result = d.removeFirst();
        assert result == 6 : "Error: Head is not correct";

        result = d.removeLast();
        assert result == -2 : "Error: Tail is not correct";

        assert d.size() == 3 : "Error: Deque is not of the correct size";

        // iterator works head to tail
        int[] expectedResult = { 5, 4, -1 };
        int index = 0;
        for (int i : d) {
            assert expectedResult[index] == i : "Incorrect content of the deque";
            index++;

        }

        assert d.size() == 3 : "Iterator unexpectedly changed the size of the deque";

        d.addFirst(7);
        d.addLast(3);

        // iterator works head to tail
        int[] expectedResponse = { 7, 5, 4, -1, 3 };
        int count = 0;
        for (int i : d) {
            assert expectedResponse[count] == i : "Incorrect content of the deque";
            count++;
        }

        Deque<Integer> deque = new Deque<Integer>();
        deque.addFirst(1);
        deque.removeFirst();

        Deque<Integer> deque_2 = new Deque<Integer>();
        deque_2.addLast(1);
        deque_2.removeLast();

        Deque<Integer> deque_3 = new Deque<Integer>();
        deque_3.addLast(1);
        deque_3.removeFirst();

        Deque<Integer> deque_4 = new Deque<Integer>();
        deque_4.addLast(1);
        deque_4.removeFirst();
        deque_4.addLast(2);
        assert deque_4.removeLast() == 2 : "Deque removeLast failed";

        Deque<Integer> deque_5 = new Deque<Integer>();
        deque_5.addLast(1);
        deque_5.removeFirst();
        deque_5.addLast(2);
        assert deque_5.removeFirst() == 2 : "Deque removeFirst failed";

    }

}
