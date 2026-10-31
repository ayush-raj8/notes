package oop.access;

// ANCHOR: static-context-fixed
/** A static method needs an object before it can touch an instance field. */
public class StaticContextFixed {
  private int n = 1;
  private static int shared = 10;

  int plusShared() {
    return n + shared;
  }

  public static void main(String[] args) {
    System.out.println(shared);
    StaticContextFixed obj = new StaticContextFixed();
    System.out.println(obj.n);
    System.out.println(obj.plusShared());
  }
}
// ANCHOR_END: static-context-fixed
