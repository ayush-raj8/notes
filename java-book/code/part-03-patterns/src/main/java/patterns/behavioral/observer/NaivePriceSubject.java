package patterns.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: naive-subject
/** Iterates the live list: an observer that detaches itself mid-notify breaks the loop. */
public class NaivePriceSubject {
  private final List<PriceObserver> observers = new ArrayList<>();

  public void attach(PriceObserver o) {
    observers.add(o);
  }

  public void detach(PriceObserver o) {
    observers.remove(o);
  }

  public void publish(String symbol, int price) {
    for (PriceObserver o : observers) {
      o.onPrice(symbol, price);
    }
  }
}
// ANCHOR_END: naive-subject
