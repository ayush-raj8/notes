package oop.generics.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: compile-ok-wildcard-read
/** Compiles: reads from ? extends, writes to ? super, null into ?. */
public final class CompileOkWildcardRead {
  public static void main(String[] args) {
    List<Integer> ints = new ArrayList<>(List.of(1, 2));
    List<? extends Number> ro = ints;
    Number n = ro.get(0);

    List<? super Integer> wo = new ArrayList<Number>();
    wo.add(5);
    Object o = wo.get(0);

    List<?> any = ints;
    any.add(null);
    System.out.println(n + " " + o + " " + any.size());
  }
}
// ANCHOR_END: compile-ok-wildcard-read
