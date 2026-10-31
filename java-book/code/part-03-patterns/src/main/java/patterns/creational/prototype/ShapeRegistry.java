package patterns.creational.prototype;

import java.util.HashMap;
import java.util.Map;

// ANCHOR: shape-registry
/** Prototype manager: callers ask for a copy by key, never touching the stored original. */
public final class ShapeRegistry {
  private final Map<String, Shape> prototypes = new HashMap<>();

  public void register(String key, Shape prototype) {
    prototypes.put(key, prototype);
  }

  public Shape create(String key) {
    Shape p = prototypes.get(key);
    if (p == null) {
      throw new IllegalArgumentException("no prototype: " + key);
    }
    return p.copy();
  }
}
// ANCHOR_END: shape-registry
