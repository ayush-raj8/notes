package patterns.behavioral.iterator;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public final class RangeDemo {
  // ANCHOR: range-demo
  public static void main(String[] args) {
    Range r = new Range(0, 10, 3);

    for (int x : r) System.out.print(x + " ");
    System.out.println("<- for-each");

    Iterator<Integer> it = r.iterator();
    while (it.hasNext()) System.out.print(it.next() + " ");
    System.out.println("<- explicit iterator");

    r.forEach(x -> System.out.print(x + " "));
    System.out.println("<- internal iteration");

    int sum = StreamSupport.stream(r.spliterator(), false).mapToInt(Integer::intValue).sum();
    System.out.println("sum=" + sum);

    try {
      it.remove();
    } catch (UnsupportedOperationException e) {
      System.out.println("default remove(): UnsupportedOperationException");
    }
  }
  // ANCHOR_END: range-demo
}
