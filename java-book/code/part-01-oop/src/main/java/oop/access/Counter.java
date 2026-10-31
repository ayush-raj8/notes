package oop.access;

// ANCHOR: counter
/** {@code created} has one copy for the class; {@code id} has one copy per object. */
public class Counter {
  private static int created = 0;
  private final int id;

  public Counter() {
    created++;
    id = created;
  }

  public int id() {
    return id;
  }

  public static int created() {
    return created;
  }

  public static void main(String[] args) {
    Counter a = new Counter();
    Counter b = new Counter();
    System.out.println(a.id() + " " + b.id() + " " + Counter.created());
  }
}
// ANCHOR_END: counter
