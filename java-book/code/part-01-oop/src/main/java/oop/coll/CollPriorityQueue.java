package oop.coll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

// ANCHOR: coll-priority-queue
class CollTask implements Comparable<CollTask> {
  final String name;
  final int priority;

  CollTask(String name, int priority) {
    this.name = name;
    this.priority = priority;
  }

  @Override
  public int compareTo(CollTask o) {
    return Integer.compare(priority, o.priority); // smaller number = served first
  }

  @Override
  public String toString() {
    return name + ":" + priority;
  }
}

public final class CollPriorityQueue {
  static <T> List<T> drain(PriorityQueue<T> pq) {
    List<T> out = new ArrayList<>();
    while (!pq.isEmpty()) {
      out.add(pq.poll());
    }
    return out;
  }

  public static void main(String[] args) {
    PriorityQueue<Integer> min = new PriorityQueue<>();
    for (int x : new int[] {5, 1, 4, 2, 3}) {
      min.offer(x);
    }
    System.out.println("toString (heap array, NOT sorted): " + min);
    System.out.println("peek=" + min.peek() + " size=" + min.size());
    System.out.println("poll order: " + drain(min));

    PriorityQueue<Integer> max = new PriorityQueue<>(Collections.reverseOrder());
    max.addAll(List.of(5, 1, 4, 2, 3));
    System.out.println("max-heap poll order: " + drain(max));

    PriorityQueue<CollTask> tasks = new PriorityQueue<>();
    tasks.add(new CollTask("write", 3));
    tasks.add(new CollTask("deploy", 5));
    tasks.add(new CollTask("fix", 1));
    System.out.println("tasks: " + drain(tasks));

    PriorityQueue<String> byLen = new PriorityQueue<>(Comparator.comparingInt(String::length));
    byLen.addAll(List.of("ccc", "a", "bb"));
    System.out.println("by length: " + drain(byLen));

    try {
      new PriorityQueue<Object>().add(new Object());
    } catch (ClassCastException e) {
      System.out.println("non-Comparable element, no comparator -> ClassCastException");
    }
  }
}
// ANCHOR_END: coll-priority-queue
