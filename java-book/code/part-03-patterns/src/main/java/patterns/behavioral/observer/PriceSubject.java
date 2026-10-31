package patterns.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: subject
/** Subject role: owns the observer list and notifies a snapshot of it. */
public class PriceSubject {
  private final List<PriceObserver> observers = new ArrayList<>();

  public void attach(PriceObserver o) {
    observers.add(o);
  }

  public void detach(PriceObserver o) {
    observers.remove(o);
  }

  public void publish(String symbol, int price) {
    for (PriceObserver o : List.copyOf(observers)) {
      o.onPrice(symbol, price);
    }
  }
}
// ANCHOR_END: subject
