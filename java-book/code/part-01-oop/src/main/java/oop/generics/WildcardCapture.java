package oop.generics;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: wildcard-capture
/** A helper method captures the wildcard so the list can be written. */
public final class WildcardCapture {
  static void swapFirstTwo(List<?> list) {
    swapHelper(list);
  }

  private static <T> void swapHelper(List<T> list) {
    T tmp = list.get(0);
    list.set(0, list.get(1));
    list.set(1, tmp);
  }

  public static void main(String[] args) {
    List<String> xs = new ArrayList<>(List.of("a", "b", "c"));
    swapFirstTwo(xs);
    System.out.println(xs);
  }
}
// ANCHOR_END: wildcard-capture
