package solutions.ch46;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** Exercise 2: iterate a list backwards with ListIterator. */
public final class SolutionReverse<T> implements Iterable<T> {
  private final List<T> list;

  SolutionReverse(List<T> list) {
    this.list = list;
  }

  @Override
  public Iterator<T> iterator() {
    ListIterator<T> li = list.listIterator(list.size());
    return new Iterator<>() {
      public boolean hasNext() {
        return li.hasPrevious();
      }

      public T next() {
        return li.previous();
      }
    };
  }

  public static void main(String[] args) {
    for (String s : new SolutionReverse<>(List.of("a", "b", "c"))) System.out.print(s);
    System.out.println();
  }
}
