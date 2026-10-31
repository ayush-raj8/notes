package patterns.behavioral.observer.exam;

import patterns.behavioral.observer.PriceSubject;

// ANCHOR: predict-throw
public final class PredictThrowAborts {
  public static void main(String[] args) {
    PriceSubject s = new PriceSubject();
    s.attach((sym, p) -> System.out.println("one"));
    s.attach(
        (sym, p) -> {
          throw new IllegalStateException("boom");
        });
    s.attach((sym, p) -> System.out.println("three"));
    try {
      s.publish("X", 1);
    } catch (IllegalStateException e) {
      System.out.println("caught " + e.getMessage());
    }
    System.out.println("end");
  }
}
// ANCHOR_END: predict-throw
