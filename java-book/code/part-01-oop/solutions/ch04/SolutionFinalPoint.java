package oop.access.solutions;

/** Exercise 2: final class, final fields, no setters, new object for each change. */
public final class SolutionFinalPoint {
  private final int x;
  private final int y;

  public SolutionFinalPoint(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public SolutionFinalPoint withX(int newX) {
    return new SolutionFinalPoint(newX, y);
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }

  public static void main(String[] args) {
    SolutionFinalPoint p = new SolutionFinalPoint(1, 2);
    SolutionFinalPoint q = p.withX(9);
    System.out.println(p + " " + q);
  }
}
