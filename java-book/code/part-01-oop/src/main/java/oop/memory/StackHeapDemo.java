package oop.memory;

// ANCHOR: stack-heap-demo
/**
 * Locals {@code a} and {@code b} live on the stack frame; {@code new Point(...)} allocates on the
 * heap. Identity comparison shows distinct objects, then aliasing after {@code b = a}.
 */
public final class StackHeapDemo {
  public static void main(String[] args) {
    Point a = new Point(0, 0);
    Point b = new Point(0, 0);
    System.out.println("initially same object? " + (a == b));
    b = a;
    System.out.println("after b=a, same object? " + (a == b));
    b.moveBy(1, 0);
    System.out.println("a=" + a + " b=" + b);
  }
}
// ANCHOR_END: stack-heap-demo
