package oop.coll.exam;

import java.util.Collections;
import java.util.PriorityQueue;

// ANCHOR: predict-priority-queue
public final class PredictPriorityQueue {
  public static void main(String[] args) {
    PriorityQueue<Integer> pq = new PriorityQueue<>();
    pq.add(4);
    pq.add(1);
    pq.add(3);
    pq.add(2);
    System.out.println(pq);
    System.out.println(pq.peek() + " " + pq.size());
    System.out.println(pq.poll() + " " + pq.poll() + " " + pq);

    PriorityQueue<Integer> max = new PriorityQueue<>(Collections.reverseOrder());
    max.add(4);
    max.add(1);
    max.add(3);
    System.out.println(max.poll() + " " + max.poll());
  }
}
// ANCHOR_END: predict-priority-queue
