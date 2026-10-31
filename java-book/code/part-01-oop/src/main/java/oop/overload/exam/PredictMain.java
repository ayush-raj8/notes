package oop.overload.exam;

// ANCHOR: predict-main
public final class PredictMain {
  public static void main(String[] args) {
    System.out.println("entry");
  }

  public static void main(int n) {
    System.out.println("other");
  }
}
// ANCHOR_END: predict-main
