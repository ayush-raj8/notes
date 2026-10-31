package oop.memory;

// ANCHOR: null-call-gotcha
/** Gotcha: the reference is null; there is no heap object to dispatch on. */
public final class NullCallGotcha {
  public static void main(String[] args) {
    Point p = null;
    try {
      System.out.println(p.toString());
    } catch (NullPointerException e) {
      System.out.println("NPE: " + e.getClass().getSimpleName());
    }
  }
}
// ANCHOR_END: null-call-gotcha
