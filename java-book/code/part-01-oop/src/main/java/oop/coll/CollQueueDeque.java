package oop.coll;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;

// ANCHOR: coll-queue-deque
public final class CollQueueDeque {
  public static void main(String[] args) {
    Queue<String> q = new LinkedList<>();
    System.out.println("empty: peek=" + q.peek() + " poll=" + q.poll()); // null, no exception
    try {
      q.remove();
    } catch (NoSuchElementException e) {
      System.out.println("empty: remove() -> NoSuchElementException");
    }
    try {
      q.element();
    } catch (NoSuchElementException e) {
      System.out.println("empty: element() -> NoSuchElementException");
    }
    q.offer("a");
    q.offer("b");
    q.offer("c");
    System.out.println("FIFO: " + q.poll() + q.poll() + q.poll());

    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(1);
    stack.push(2);
    stack.push(3);
    System.out.println("Deque as stack: " + stack + " peek=" + stack.peek() + " pop=" + stack.pop());

    Stack<Integer> legacy = new Stack<>(); // extends Vector: synchronized, legacy
    legacy.push(1);
    legacy.push(2);
    legacy.push(3);
    System.out.println("legacy Stack  : " + legacy + " peek=" + legacy.peek());

    Deque<String> d = new ArrayDeque<>();
    d.offerFirst("m");
    d.offerLast("z");
    d.offerFirst("a");
    System.out.println("both ends: " + d + " pollFirst=" + d.pollFirst() + " pollLast=" + d.pollLast());

    try {
      new ArrayDeque<String>().add(null);
    } catch (NullPointerException e) {
      System.out.println("ArrayDeque.add(null) -> NullPointerException");
    }
  }
}
// ANCHOR_END: coll-queue-deque
