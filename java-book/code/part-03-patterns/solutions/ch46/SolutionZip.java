package solutions.ch46;

import java.util.Iterator;
import java.util.List;

/** Exercise 3: walk two iterables in lock step, stop at the shorter. */
public final class SolutionZip {
  static <A, B> void zip(Iterable<A> as, Iterable<B> bs) {
    Iterator<A> ia = as.iterator();
    Iterator<B> ib = bs.iterator();
    while (ia.hasNext() && ib.hasNext()) {
      System.out.println(ia.next() + "=" + ib.next());
    }
  }

  public static void main(String[] args) {
    zip(List.of("x", "y", "z"), List.of(1, 2));
  }
}
