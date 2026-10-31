package patterns.behavioral.observer.exam;

import patterns.behavioral.observer.PriceObserver;
import patterns.behavioral.observer.PriceSubject;

// ANCHOR: predict-snapshot
public final class PredictSnapshot {
  public static void main(String[] args) {
    PriceSubject s = new PriceSubject();
    PriceObserver[] a = new PriceObserver[1];
    a[0] =
        (sym, p) -> {
          System.out.print("A ");
          s.detach(a[0]);
        };
    s.attach(a[0]);
    s.attach((sym, p) -> System.out.print("B "));
    s.publish("X", 1);
    s.publish("X", 2);
    System.out.println();
  }
}
// ANCHOR_END: predict-snapshot
