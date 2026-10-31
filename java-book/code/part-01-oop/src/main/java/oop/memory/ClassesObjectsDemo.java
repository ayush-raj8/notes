package oop.memory;

// ANCHOR: classes-objects-demo
public final class ClassesObjectsDemo {
  public static void main(String[] args) {
    Point a = new Point(1, 2);
    Point b = new Point(1, 2);
    System.out.println("a=" + a + " b=" + b);
    System.out.println("a == b ? " + (a == b));
    System.out.println("same identity? " + (System.identityHashCode(a) == System.identityHashCode(b)));
  }
}
// ANCHOR_END: classes-objects-demo
