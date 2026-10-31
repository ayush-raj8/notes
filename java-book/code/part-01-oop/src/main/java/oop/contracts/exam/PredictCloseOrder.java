package oop.contracts.exam;

// ANCHOR: predict-close-order
public final class PredictCloseOrder {
  static final class R implements AutoCloseable {
    final String n;

    R(String n) {
      this.n = n;
      System.out.println("new " + n);
    }

    @Override
    public void close() {
      System.out.println("close " + n);
    }
  }

  public static void main(String[] args) {
    try (R x = new R("x"); R y = new R("y")) {
      System.out.println("body");
    } finally {
      System.out.println("finally");
    }
  }
}
// ANCHOR_END: predict-close-order
