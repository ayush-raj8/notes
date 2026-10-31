package oop.contracts;

import java.util.Iterator;
import java.util.NoSuchElementException;

// ANCHOR: iterable-demo
public final class IterableDemo {
  static final class Range implements Iterable<Integer> {
    private final int from;
    private final int to;

    Range(int from, int to) {
      this.from = from;
      this.to = to;
    }

    @Override
    public Iterator<Integer> iterator() {
      return new Iterator<>() {
        private int next = from;

        @Override
        public boolean hasNext() {
          return next < to;
        }

        @Override
        public Integer next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return next++;
        }
      };
    }
  }

  public static void main(String[] args) {
    Range r = new Range(1, 4);
    for (int x : r) {
      System.out.print(x + " ");
    }
    System.out.println();

    Iterator<Integer> it = r.iterator();
    while (it.hasNext()) {
      System.out.print(it.next() + ";");
    }
    System.out.println();

    r.forEach(x -> System.out.print(x * x + ","));
    System.out.println();

    try {
      it.next();
    } catch (NoSuchElementException e) {
      System.out.println("NoSuchElementException");
    }

    try {
      r.iterator().remove();
    } catch (UnsupportedOperationException e) {
      System.out.println("remove: " + e.getMessage());
    }
  }
}
// ANCHOR_END: iterable-demo
