package lld.scheduler.exam;

import java.util.PriorityQueue;

// ANCHOR: predict-queue-order
public class PredictQueueOrder {
  record Job(String name) {}

  public static void main(String[] args) {
    PriorityQueue<Integer> q = new PriorityQueue<>();
    for (int n : new int[] {5, 1, 4, 2, 3}) {
      q.add(n);
    }
    System.out.println(q);
    System.out.println(q.peek() + " " + q.size());
    StringBuilder sb = new StringBuilder();
    while (!q.isEmpty()) {
      sb.append(q.poll());
    }
    System.out.println(sb);
    PriorityQueue<Job> jobs = new PriorityQueue<>();
    try {
      jobs.add(new Job("a"));
      System.out.println("added one");
      jobs.add(new Job("b"));
    } catch (ClassCastException e) {
      System.out.println(e.getClass().getSimpleName());
    }
  }
}
// ANCHOR_END: predict-queue-order
