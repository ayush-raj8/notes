package oop.ex;

// ANCHOR: ex-twr-order
public final class ExTwrOrder {
  static ExRes open(String name, boolean fail) {
    if (fail) {
      throw new IllegalStateException("cannot open " + name);
    }
    return new ExRes(name, false);
  }

  public static void main(String[] args) {
    System.out.println("== second resource fails to open");
    try (ExRes a = open("A", false);
        ExRes b = open("B", true)) {
      System.out.println("body never runs");
    } catch (IllegalStateException e) {
      System.out.println("caught " + e.getMessage());
    } finally {
      System.out.println("finally after resources closed");
    }

    System.out.println("== existing effectively-final variable (Java 9+)");
    ExRes shared = new ExRes("S", false);
    try (shared) {
      System.out.println("body");
    }

    System.out.println("== catch and finally run after close");
    try (ExRes r = new ExRes("R", false)) {
      throw new IllegalArgumentException("in body");
    } catch (IllegalArgumentException e) {
      System.out.println("catch " + e.getMessage());
    } finally {
      System.out.println("finally");
    }
  }
}
// ANCHOR_END: ex-twr-order
