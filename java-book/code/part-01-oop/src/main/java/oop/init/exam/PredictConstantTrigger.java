package oop.init.exam;

// ANCHOR: predict-constant-trigger
class K {
  static final int A = 5;
  static final Integer B = 6;

  static {
    System.out.println("K init");
  }
}

public final class PredictConstantTrigger {
  public static void main(String[] args) {
    System.out.println(K.A);
    System.out.println(K.B);
  }
}
// ANCHOR_END: predict-constant-trigger
