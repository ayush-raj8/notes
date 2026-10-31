package patterns.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

// ANCHOR: tree-factory
/** Flyweight factory: returns the cached instance for a key, creating it once. */
public final class TreeFactory {
  private static final Map<String, TreeType> CACHE = new HashMap<>();
  private static int created;

  private TreeFactory() {}

  public static TreeType get(String name, String color) {
    return CACHE.computeIfAbsent(
        name + ":" + color,
        k -> {
          created++;
          return new TreeType(name, color);
        });
  }

  public static int created() {
    return created;
  }

  public static void reset() {
    CACHE.clear();
    created = 0;
  }
}
// ANCHOR_END: tree-factory
