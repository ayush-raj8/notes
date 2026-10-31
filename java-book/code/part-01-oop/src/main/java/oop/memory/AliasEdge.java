package oop.memory;

// ANCHOR: alias-edge
/** Edge: two stack variables can alias one heap object. */
public final class AliasEdge {
  public static void main(String[] args) {
    Point first = new Point(5, 5);
    Point second = first;
    second.moveBy(1, 0);
    System.out.println("first=" + first);
    System.out.println("second=" + second);
    System.out.println("same object? " + (first == second));
  }
}
// ANCHOR_END: alias-edge
