package oop.access.exam;

// ANCHOR: predict-static-shared
public final class PredictStaticShared {
  static class Box {
    static int x;
  }

  public static void main(String[] args) {
    Box a = new Box();
    Box b = new Box();
    a.x = 5;
    b.x++;
    System.out.println(a.x + " " + b.x);
  }
}
// ANCHOR_END: predict-static-shared
