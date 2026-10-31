package oop.access.exam;

// ANCHOR: predict-static-counter
public final class PredictStaticCounter {
  static int count;
  int id;

  PredictStaticCounter() {
    id = ++count;
  }

  public static void main(String[] args) {
    new PredictStaticCounter();
    new PredictStaticCounter();
    PredictStaticCounter c = new PredictStaticCounter();
    System.out.println(c.id + " " + count);
  }
}
// ANCHOR_END: predict-static-counter
