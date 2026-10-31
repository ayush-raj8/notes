package solutions.ch46;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/** Exercise 4: wrap an iterator and skip elements; hasNext must look ahead without consuming. */
public final class SolutionFilterIterator<T> implements Iterator<T> {
  private final Iterator<T> source;
  private final Predicate<T> keep;
  private T pending;
  private boolean hasPending;

  SolutionFilterIterator(Iterator<T> source, Predicate<T> keep) {
    this.source = source;
    this.keep = keep;
  }

  @Override
  public boolean hasNext() {
    while (!hasPending && source.hasNext()) {
      T candidate = source.next();
      if (keep.test(candidate)) {
        pending = candidate;
        hasPending = true;
      }
    }
    return hasPending;
  }

  @Override
  public T next() {
    if (!hasNext()) throw new NoSuchElementException();
    hasPending = false;
    return pending;
  }

  public static void main(String[] args) {
    Iterator<Integer> evens =
        new SolutionFilterIterator<>(List.of(1, 2, 3, 4, 5, 6).iterator(), n -> n % 2 == 0);
    System.out.println(evens.hasNext() + " " + evens.hasNext());
    while (evens.hasNext()) System.out.print(evens.next() + " ");
    System.out.println();
  }
}
