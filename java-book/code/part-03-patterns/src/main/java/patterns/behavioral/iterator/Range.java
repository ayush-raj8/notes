package patterns.behavioral.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

// ANCHOR: range
/** Aggregate role: {@code Iterable} creates a fresh iterator per call. */
public final class Range implements Iterable<Integer> {
  private final int start;
  private final int end;
  private final int step;

  public Range(int start, int end, int step) {
    if (step <= 0) throw new IllegalArgumentException("step must be > 0");
    this.start = start;
    this.end = end;
    this.step = step;
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<>() {
      private int current = start;

      @Override
      public boolean hasNext() {
        return current < end;
      }

      @Override
      public Integer next() {
        if (!hasNext()) throw new NoSuchElementException();
        int v = current;
        current += step;
        return v;
      }
    };
  }
}
// ANCHOR_END: range
