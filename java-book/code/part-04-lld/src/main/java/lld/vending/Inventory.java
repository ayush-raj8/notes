package lld.vending;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// ANCHOR: inventory
/** Slots keyed by product code. Not thread-safe by itself: the machine's lock guards it. */
public final class Inventory {
  private record Slot(Product product, int quantity) {}

  private final Map<String, Slot> slots = new HashMap<>();

  public void load(Product p, int quantity) {
    slots.merge(p.code(), new Slot(p, quantity),
        (old, add) -> new Slot(p, old.quantity() + add.quantity()));
  }

  public Optional<Product> find(String code) {
    return Optional.ofNullable(slots.get(code)).map(Slot::product);
  }

  public int quantity(String code) {
    Slot s = slots.get(code);
    return s == null ? 0 : s.quantity();
  }

  public void take(String code) {
    Slot s = slots.get(code);
    if (s == null || s.quantity() == 0) {
      throw new IllegalStateException("nothing to take from " + code);
    }
    slots.put(code, new Slot(s.product(), s.quantity() - 1));
  }
}
// ANCHOR_END: inventory
