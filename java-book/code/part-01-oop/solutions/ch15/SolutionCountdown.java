package oop.contracts.solutions;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Iterable that counts down from n to 1. */
public final class SolutionCountdown implements Iterable<Integer> {
  private final int n;

  public SolutionCountdown(int n) {
    this.n = n;
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<>() {
      private int cur = n;

      @Override
      public boolean hasNext() {
        return cur >= 1;
      }

      @Override
      public Integer next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return cur--;
      }
    };
  }

  public static void main(String[] args) {
    for (int x : new SolutionCountdown(3)) {
      System.out.println(x);
    }
  }
}
