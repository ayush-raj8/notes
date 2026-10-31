package solid.lsp.solutions;

/** Exercise 5: test a candidate subtype against the Shape contract (scaled(k).area() == k*k*area()). */
public final class SolutionTriangleContract {
  /** Right triangle with legs a and b; integer area is a * b / 2 rounded down. */
  record Triangle(int a, int b) {
    int area() {
      return a * b / 2;
    }

    Triangle scaled(int k) {
      return new Triangle(a * k, b * k);
    }
  }

  static boolean keepsScalingLaw(Triangle t, int k) {
    return t.scaled(k).area() == k * k * t.area();
  }

  public static void main(String[] args) {
    System.out.println("4,3 -> " + keepsScalingLaw(new Triangle(4, 3), 2));
    Triangle odd = new Triangle(3, 3);
    System.out.println("3,3 -> " + keepsScalingLaw(odd, 2) + " (" + odd.scaled(2).area() + " vs " + 4 * odd.area() + ")");
  }
}
