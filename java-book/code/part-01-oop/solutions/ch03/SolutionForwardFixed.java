package oop.init.solutions;

/** Exercise 5: reorder the declarations so the forward reference disappears. */
public final class SolutionForwardFixed {
  static int b = 2;
  static int a = b + 1;

  public static void main(String[] args) {
    System.out.println("a=" + a + ", b=" + b);
  }
}
