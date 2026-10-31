package oop.immut;

// ANCHOR: record-basics
/** Java 16+: a record is a final class with final fields and generated members. */
public final class RecordBasics {
  interface Shape {
    double area();
  }

  record Point(int x, int y) {
    static final Point ORIGIN = new Point(0, 0);

    Point(int both) {
      this(both, both);
    }

    static Point of(int x, int y) {
      return new Point(x, y);
    }

    int manhattan() {
      return Math.abs(x) + Math.abs(y);
    }
  }

  record Rect(double w, double h) implements Shape {
    public double area() {
      return w * h;
    }
  }

  public static void main(String[] args) {
    Point p = new Point(3, -4);
    System.out.println(p);
    System.out.println("x()=" + p.x() + " y()=" + p.y());
    System.out.println("equals=" + p.equals(Point.of(3, -4)));
    System.out.println("hash equal=" + (p.hashCode() == new Point(3, -4).hashCode()));
    System.out.println("manhattan=" + p.manhattan());
    System.out.println("one-arg=" + new Point(2));
    System.out.println("ORIGIN=" + Point.ORIGIN);
    System.out.println("area=" + new Rect(2, 3).area());
  }
}
// ANCHOR_END: record-basics
