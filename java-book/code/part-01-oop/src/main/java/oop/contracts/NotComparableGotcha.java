package oop.contracts;

import java.util.Set;
import java.util.TreeSet;

// ANCHOR: not-comparable
public final class NotComparableGotcha {
  static final class Plain {}

  public static void main(String[] args) {
    Set<Plain> set = new TreeSet<>();
    try {
      set.add(new Plain());
    } catch (ClassCastException e) {
      System.out.println("ClassCastException");
      System.out.println(e.getMessage().contains("cannot be cast to class java.lang.Comparable"));
    }
  }
}
// ANCHOR_END: not-comparable
