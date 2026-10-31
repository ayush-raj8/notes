package oop.init.exam;

// ANCHOR: predict-static-main
public final class PredictStaticMain {
  static {
    System.out.println("static");
  }

  public static void main(String[] args) {
    System.out.println("main");
  }
}
// ANCHOR_END: predict-static-main
