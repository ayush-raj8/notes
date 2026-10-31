package oop.overload.solutions;

public final class SolutionOverload {
  public static String max(int a, int b) {
    return "int:" + Math.max(a, b);
  }

  public static String max(double a, double b) {
    return "double:" + Math.max(a, b);
  }

  public static void main(String[] args) {
    System.out.println(max(1, 2));
    System.out.println(max(1.5, 2.5));
  }
}
