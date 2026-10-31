package patterns.behavioral.observer.exam;

import patterns.behavioral.observer.NaivePriceSubject;
import patterns.behavioral.observer.PriceObserver;

// ANCHOR: predict-naive-two
public final class PredictNaiveTwo {
  public static void main(String[] args) {
    NaivePriceSubject s = new NaivePriceSubject();
    PriceObserver[] a = new PriceObserver[1];
    a[0] =
        (sym, p) -> {
          System.out.print("A ");
          s.detach(a[0]);
        };
    s.attach(a[0]);
    s.attach((sym, p) -> System.out.print("B "));
    s.publish("X", 1);
    System.out.println("done");
  }
}
// ANCHOR_END: predict-naive-two
