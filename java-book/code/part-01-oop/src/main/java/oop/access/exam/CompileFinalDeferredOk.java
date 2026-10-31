package oop.access.exam;

// ANCHOR: compile-final-deferred
public final class CompileFinalDeferredOk {
  public static void main(String[] args) {
    final int x;
    if (args.length > 0) {
      x = 1;
    } else {
      x = 2;
    }
    System.out.println(x);
  }
}
// ANCHOR_END: compile-final-deferred
