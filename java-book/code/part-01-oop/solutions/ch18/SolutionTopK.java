package oop.coll.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/** Exercise 4: the k largest values using a min-heap of size k (O(n log k)). */
public final class SolutionTopK {
  static List<Integer> topK(int[] data, int k) {
    PriorityQueue<Integer> heap = new PriorityQueue<>(); // smallest of the kept values on top
    for (int x : data) {
      heap.offer(x);
      if (heap.size() > k) {
        heap.poll(); // drop the smallest
      }
    }
    List<Integer> out = new ArrayList<>(heap);
    out.sort(Collections.reverseOrder());
    return out;
  }

  public static void main(String[] args) {
    System.out.println(topK(new int[] {7, 2, 9, 4, 11, 5, 9}, 3));
  }
}
