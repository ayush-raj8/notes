package oop.memory;

// ANCHOR: primitive-vs-ref-gotcha
/** Gotcha: primitives are copied by value; object references share the heap object when aliased. */
public final class PrimitiveVsRefGotcha {
  static void bumpInt(int n) {
    n = n + 1;
  }

  static void bumpPoint(Point p) {
    p.moveBy(1, 0);
  }

  public static void main(String[] args) {
    int n = 3;
    bumpInt(n);
    System.out.println("n=" + n);

    Point p = new Point(3, 0);
    bumpPoint(p);
    System.out.println("p=" + p);
  }
}
// ANCHOR_END: primitive-vs-ref-gotcha
