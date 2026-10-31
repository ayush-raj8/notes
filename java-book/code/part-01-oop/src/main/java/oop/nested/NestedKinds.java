package oop.nested;

// ANCHOR: nested-kinds
/** Nested enum, record and interface are implicitly static: no outer instance (records: Java 16+). */
public final class NestedKinds {
  private final int instanceField = 99;
  private static final int STATIC_FIELD = 7;

  enum Level {
    LOW,
    HIGH;

    int boost() {
      return STATIC_FIELD + ordinal(); // outer static member: allowed
    }
  }

  record Point(int x, int y) {
    int sum() {
      return x + y + STATIC_FIELD;
    }
  }

  interface Visitor {
    String visit(Point p);
  }

  static String run() {
    Visitor v = p -> "(" + p.x() + "," + p.y() + ")";
    return v.visit(new Point(1, 2)) + " " + Level.HIGH.boost() + " " + new Point(1, 2).sum();
  }

  int peek() {
    return instanceField;
  }

  public static void main(String[] args) {
    System.out.println(run());
    System.out.println(new NestedKinds().peek());
  }
}
// ANCHOR_END: nested-kinds
