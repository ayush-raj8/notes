package oop.nested.exam;

// ANCHOR: predict-shadow
public final class PredictShadow {
  int v = 10;

  class In {
    int v = 20;

    void print(int v) {
      System.out.println(v);
      System.out.println(this.v);
      System.out.println(PredictShadow.this.v);
    }
  }

  public static void main(String[] args) {
    new PredictShadow().new In().print(30);
  }
}
// ANCHOR_END: predict-shadow
