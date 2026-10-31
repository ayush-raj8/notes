package patterns.structural.facade;

import java.util.HashMap;
import java.util.Map;

// ANCHOR: facade-inventory
public class Inventory {
  private final Map<String, Integer> stock = new HashMap<>(Map.of("pen", 1, "ink", 5));

  public boolean reserve(String sku) {
    int left = stock.getOrDefault(sku, 0);
    if (left == 0) {
      return false;
    }
    stock.put(sku, left - 1);
    return true;
  }

  public void release(String sku) {
    stock.merge(sku, 1, Integer::sum);
  }

  public int left(String sku) {
    return stock.getOrDefault(sku, 0);
  }
}
// ANCHOR_END: facade-inventory
