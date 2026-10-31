package oop.abstracts.exam;

// ANCHOR: predict-anon-abstract
abstract class PaaCounter {
  private int n;

  PaaCounter(int start) {
    n = start;
  }

  abstract int step();

  int next() {
    n += step();
    return n;
  }
}

public final class PredictAnonAbstract {
  public static void main(String[] args) {
    PaaCounter c = new PaaCounter(10) {
      @Override
      int step() {
        return 5;
      }
    };
    System.out.println(c.next());
    System.out.println(c.next());
    System.out.println(c.getClass().getSuperclass().getSimpleName());
    System.out.println(c.getClass().isAnonymousClass());
  }
}
// ANCHOR_END: predict-anon-abstract
