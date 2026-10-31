package oop.memory;

// ANCHOR: pass-by-value
/**
 * Java passes the reference by value. Rebinding the parameter does not rebind the caller's
 * variable; mutating the shared object is visible to the caller.
 */
public final class PassByValueDemo {
  static void rebind(Point p) {
    p = new Point(9, 9);
  }

  static void mutate(Point p) {
    p.moveBy(10, 0);
  }

  public static void main(String[] args) {
    Point origin = new Point(0, 0);
    System.out.println("before rebind: " + origin);
    rebind(origin);
    System.out.println("after rebind:  " + origin);

    System.out.println("before mutate: " + origin);
    mutate(origin);
    System.out.println("after mutate:  " + origin);
  }
}
// ANCHOR_END: pass-by-value
