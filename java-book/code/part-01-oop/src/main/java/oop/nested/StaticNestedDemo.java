package oop.nested;

// ANCHOR: static-nested
/** A static nested class is a member of the outer class but has no outer instance. */
public final class StaticNestedDemo {
  private static int created = 0;

  static final class Counter {
    private int n;

    int next() {
      created++; // outer private static: allowed
      return ++n;
    }
  }

  static String describe(Counter c) {
    return "n=" + c.n + " created=" + created; // outer reads nested private
  }

  public static void main(String[] args) {
    Counter c = new Counter(); // no outer instance needed
    c.next();
    c.next();
    System.out.println(describe(c));
    StaticNestedDemo.Counter d = new StaticNestedDemo.Counter();
    System.out.println(d.next());
  }
}
// ANCHOR_END: static-nested
