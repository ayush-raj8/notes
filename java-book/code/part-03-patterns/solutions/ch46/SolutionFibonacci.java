package solutions.ch46;

import java.util.Iterator;

/** Exercise 1: a bounded Fibonacci Iterable (an iterator may be computed, not stored). */
public final class SolutionFibonacci implements Iterable<Long> {
  private final int count;

  SolutionFibonacci(int count) {
    this.count = count;
  }

  @Override
  public Iterator<Long> iterator() {
    return new Iterator<>() {
      private int produced;
      private long a = 0;
      private long b = 1;

      public boolean hasNext() {
        return produced < count;
      }

      public Long next() {
        long v = a;
        a = b;
        b = v + b;
        produced++;
        return v;
      }
    };
  }

  public static void main(String[] args) {
    for (long f : new SolutionFibonacci(10)) System.out.print(f + " ");
    System.out.println();
  }
}
