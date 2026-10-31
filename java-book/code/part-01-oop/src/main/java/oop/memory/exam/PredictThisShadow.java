package oop.memory.exam;

// ANCHOR: predict-this-shadow
public final class PredictThisShadow {
  private int n = 1;

  void set(int n) {
    n = 5;
  }

  public static void main(String[] args) {
    PredictThisShadow o = new PredictThisShadow();
    o.set(9);
    System.out.println(o.n);
  }
}
// ANCHOR_END: predict-this-shadow
