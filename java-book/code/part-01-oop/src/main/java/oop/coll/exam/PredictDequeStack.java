package oop.coll.exam;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

// ANCHOR: predict-deque-stack
public final class PredictDequeStack {
  public static void main(String[] args) {
    Deque<Integer> d = new ArrayDeque<>();
    d.push(1);
    d.push(2);
    d.addLast(3);
    d.offerFirst(4);
    System.out.println(d);
    System.out.println(d.pop() + " " + d.pollLast() + " " + d.peekFirst() + " " + d);

    Queue<Integer> q = new LinkedList<>();
    q.add(1);
    q.offer(2);
    System.out.println(q.peek() + " " + q.poll() + " " + q.poll() + " " + q.poll());
  }
}
// ANCHOR_END: predict-deque-stack
