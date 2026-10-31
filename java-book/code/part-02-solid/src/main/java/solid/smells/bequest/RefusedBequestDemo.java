package solid.smells.bequest;

import java.util.HashMap;
import java.util.Map;

public final class RefusedBequestDemo {
  // ANCHOR: bequest-before
  /** Extends HashMap only to reuse storage, and inherits put/remove/clear that skip its rule. */
  public static final class InventoryBefore extends HashMap<String, Integer> {
    public void stock(String sku, int n) {
      if (n <= 0) {
        throw new IllegalArgumentException("n must be positive");
      }
      merge(sku, n, Integer::sum);
    }

    public int count(String sku) {
      return getOrDefault(sku, 0);
    }
  }
  // ANCHOR_END: bequest-before

  // ANCHOR: bequest-after
  /** Has-a map: only stock and count exist. */
  public static final class Inventory {
    private final Map<String, Integer> items = new HashMap<>();

    public void stock(String sku, int n) {
      if (n <= 0) {
        throw new IllegalArgumentException("n must be positive");
      }
      items.merge(sku, n, Integer::sum);
    }

    public int count(String sku) {
      return items.getOrDefault(sku, 0);
    }
  }
  // ANCHOR_END: bequest-after

  public static void main(String[] args) {
    // ANCHOR: bequest-main
    InventoryBefore before = new InventoryBefore();
    before.stock("pen", 5);
    before.put("pen", -3); // bypasses the rule
    System.out.println("before count=" + before.count("pen"));
    before.clear();
    System.out.println("before after clear=" + before.count("pen"));

    Inventory after = new Inventory();
    after.stock("pen", 5);
    try {
      after.stock("pen", -3);
    } catch (IllegalArgumentException e) {
      System.out.println("after rejected: " + e.getMessage());
    }
    System.out.println("after count=" + after.count("pen"));
    // ANCHOR_END: bequest-main
  }
}
