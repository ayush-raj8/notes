package oop.access.exam;

// ANCHOR: predict-private-same-class
public final class PredictPrivateSameClass {
  private final int v;

  PredictPrivateSameClass(int v) {
    this.v = v;
  }

  boolean same(PredictPrivateSameClass o) {
    return v == o.v;
  }

  public static void main(String[] args) {
    System.out.println(new PredictPrivateSameClass(3).same(new PredictPrivateSameClass(3)));
    System.out.println(new PredictPrivateSameClass(3).same(new PredictPrivateSameClass(4)));
  }
}
// ANCHOR_END: predict-private-same-class
