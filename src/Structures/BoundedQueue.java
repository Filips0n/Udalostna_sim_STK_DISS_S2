package Structures;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class BoundedQueue<T> {
    private Queue<T> queue;
    private int maxSize;

    public BoundedQueue(int maxSize) {
        this.maxSize = maxSize;
        queue = new LinkedList<>();
    }

    public void add(T element) throws IllegalStateException {
        if (queue.size() >= maxSize) {
            throw new IllegalStateException("Queue is full");
        }
        queue.add(element);
    }

    public T poll() {
        return queue.poll();
    }

    public T peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public boolean isFull() {
        return queue.size() == maxSize;
    }

    public void clear() {
        this.queue.clear();
    }

    public Queue<T> getData() {
        return this.queue;
    }
}

