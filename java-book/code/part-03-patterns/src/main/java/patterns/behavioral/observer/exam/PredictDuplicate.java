package patterns.behavioral.observer.exam;

import patterns.behavioral.observer.PriceObserver;
import patterns.behavioral.observer.PriceSubject;

// ANCHOR: predict-duplicate
public final class PredictDuplicate {
  public static void main(String[] args) {
    PriceSubject s = new PriceSubject();
    PriceObserver o = (sym, p) -> System.out.print("hit ");
    s.attach(o);
    s.attach(o);
    s.publish("X", 1);
    s.detach(o);
    s.publish("X", 2);
    System.out.println();
  }
}
// ANCHOR_END: predict-duplicate
