package oop.ex.exam;

// ANCHOR: predict-twr
class Res5 implements AutoCloseable {
  private final String name;
  private final boolean fail;

  Res5(String name, boolean fail) {
    this.name = name;
    this.fail = fail;
    System.out.println("open " + name);
  }

  @Override
  public void close() {
    System.out.println("close " + name);
    if (fail) {
      throw new IllegalStateException("close-" + name);
    }
  }
}

public final class PredictTwr {
  public static void main(String[] args) {
    try (Res5 a = new Res5("A", true);
        Res5 b = new Res5("B", false)) {
      System.out.println("body");
      throw new RuntimeException("body-ex");
    } catch (RuntimeException e) {
      System.out.println("catch " + e.getMessage() + " suppressed=" + e.getSuppressed().length);
    } finally {
      System.out.println("finally");
    }
  }
}
// ANCHOR_END: predict-twr
