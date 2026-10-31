package oop.init.solutions;

class Risky {
  static final int VALUE = compute();

  static int compute() {
    throw new IllegalStateException("cannot start");
  }
}

/** Exercise 3: catch the error raised by a failing static initializer. */
public final class SolutionInitFailure {
  public static void main(String[] args) {
    try {
      System.out.println(Risky.VALUE);
    } catch (ExceptionInInitializerError e) {
      System.out.println("cause: " + e.getCause().getMessage());
    }
  }
}
