package oop.ctors.solutions;

/** Exercise: overloaded constructors with this() chaining. */
public final class SolutionRect {
  private final int w;
  private final int h;

  public SolutionRect() {
    this(1, 1);
  }

  public SolutionRect(int side) {
    this(side, side);
  }

  public SolutionRect(int w, int h) {
    this.w = w;
    this.h = h;
  }

  public int area() {
    return w * h;
  }

  public static void main(String[] args) {
    System.out.println(new SolutionRect().area());
    System.out.println(new SolutionRect(3).area());
    System.out.println(new SolutionRect(2, 5).area());
  }
}
