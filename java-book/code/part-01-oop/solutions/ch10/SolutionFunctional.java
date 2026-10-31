package oop.abstracts.solutions;

/** Exercise 3: own functional interface with a default method, used as lambda and method ref. */
public final class SolutionFunctional {
  @FunctionalInterface
  interface TriFunction {
    int apply(int a, int b, int c);

    default TriFunction plusOne() {
      return (a, b, c) -> apply(a, b, c) + 1;
    }
  }

  static int sum3(int a, int b, int c) {
    return a + b + c;
  }

  public static void main(String[] args) {
    TriFunction max = (a, b, c) -> Math.max(a, Math.max(b, c));
    TriFunction sum = SolutionFunctional::sum3;
    System.out.println(max.apply(1, 9, 4));
    System.out.println(sum.plusOne().apply(1, 2, 3));
  }
}
