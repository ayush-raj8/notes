package oop.ex.solutions;

/** Two resources, both closing; print suppressed exceptions. */
public final class SolutionCloseBoth {
  static final class R implements AutoCloseable {
    private final String n;

    R(String n) {
      this.n = n;
    }

    @Override
    public void close() {
      throw new IllegalStateException("close " + n);
    }
  }

  public static void main(String[] args) {
    try (R a = new R("a"); R b = new R("b")) {
      throw new IllegalArgumentException("body");
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
      for (Throwable t : e.getSuppressed()) {
        System.out.println("suppressed: " + t.getMessage());
      }
    }
  }
}
