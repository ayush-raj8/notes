package oop.access.solutions;

import static java.lang.Math.PI;
import static java.lang.Math.pow;

/** Exercise 3: circle area with static imports. */
public final class SolutionStaticImport {
  static double area(double r) {
    return PI * pow(r, 2);
  }

  public static void main(String[] args) {
    System.out.println(area(2));
  }
}
