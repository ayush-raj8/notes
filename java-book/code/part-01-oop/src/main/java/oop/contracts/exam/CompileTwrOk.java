package oop.contracts.exam;

// ANCHOR: compile-twr-ok
public final class CompileTwrOk {
  static final class R implements AutoCloseable {
    @Override
    public void close() {
      System.out.println("closed");
    }
  }

  public static void main(String[] args) {
    try (R r = new R()) {
      System.out.println("body");
    }
  }
}
// ANCHOR_END: compile-twr-ok
