package oop.memory.solutions;

/**
 * Exercise solution (not on Maven path unless copied): a tiny box with {@code this} setters.
 * Kept under solutions/ for reading; compile manually if desired.
 */
public final class SolutionBox {
  private int w;
  private int h;

  public SolutionBox width(int w) {
    this.w = w;
    return this;
  }

  public SolutionBox height(int h) {
    this.h = h;
    return this;
  }

  public int area() {
    return w * h;
  }

  public static void main(String[] args) {
    SolutionBox b = new SolutionBox().width(3).height(4);
    System.out.println(b.area());
  }
}
