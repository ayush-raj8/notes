package oop.access;

// ANCHOR: final-demo
/** {@code final} on a constant, a blank final field, a parameter, and a local. */
public final class FinalDemo {
  public static final int MAX = 3;
  private final int id;

  public FinalDemo(int id) {
    this.id = id;
  }

  public int add(final int x) {
    final int y = x + id;
    return y;
  }

  public static void main(String[] args) {
    FinalDemo d = new FinalDemo(10);
    System.out.println(d.add(5));
    System.out.println(MAX);
  }
}
// ANCHOR_END: final-demo
