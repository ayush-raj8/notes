package oop.ex.solutions;

/** Rewrite so the exception is not lost: no return in finally. */
public final class SolutionFinallyTrace {
  static int compute() {
    int result = -1;
    try {
      result = Integer.parseInt("12");
      return result;
    } finally {
      System.out.println("cleanup");
    }
  }

  public static void main(String[] args) {
    System.out.println(compute());
    try {
      try {
        Integer.parseInt("x");
      } finally {
        System.out.println("cleanup runs, exception still propagates");
      }
    } catch (NumberFormatException e) {
      System.out.println("caller sees " + e.getClass().getSimpleName());
    }
  }
}
