package oop.contracts.exam;

// ANCHOR: predict-close-throws
public final class PredictCloseThrows {
  static final class R implements AutoCloseable {
    @Override
    public void close() {
      throw new IllegalStateException("close");
    }
  }

  public static void main(String[] args) {
    try (R r = new R()) {
      throw new IllegalArgumentException("body");
    } catch (RuntimeException e) {
      System.out.println(e.getMessage() + " " + e.getSuppressed().length);
    }
  }
}
// ANCHOR_END: predict-close-throws
