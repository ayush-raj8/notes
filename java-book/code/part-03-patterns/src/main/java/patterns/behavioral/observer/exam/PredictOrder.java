package patterns.behavioral.observer.exam;

import patterns.behavioral.observer.PriceSubject;

// ANCHOR: predict-order
public final class PredictOrder {
  public static void main(String[] args) {
    PriceSubject s = new PriceSubject();
    s.attach((sym, p) -> System.out.print("A "));
    s.attach((sym, p) -> System.out.print("B "));
    s.attach((sym, p) -> System.out.print("C "));
    s.publish("X", 1);
    System.out.println();
  }
}
// ANCHOR_END: predict-order
