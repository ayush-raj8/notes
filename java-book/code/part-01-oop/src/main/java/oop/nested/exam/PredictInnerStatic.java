package oop.nested.exam;

// ANCHOR: predict-inner-static
/** Java 16+: an inner class may declare static members. Older papers say it may not. */
public final class PredictInnerStatic {
  class Inner {
    static int count;

    Inner() {
      count++;
    }
  }

  public static void main(String[] args) {
    PredictInnerStatic a = new PredictInnerStatic();
    PredictInnerStatic b = new PredictInnerStatic();
    a.new Inner();
    b.new Inner();
    b.new Inner();
    System.out.println(Inner.count);
  }
}
// ANCHOR_END: predict-inner-static
