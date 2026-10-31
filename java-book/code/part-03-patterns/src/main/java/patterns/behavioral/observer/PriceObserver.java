package patterns.behavioral.observer;

// ANCHOR: observer-iface
/** Observer role: receives a push of (symbol, price). */
@FunctionalInterface
public interface PriceObserver {
  void onPrice(String symbol, int price);
}
// ANCHOR_END: observer-iface
