package patterns.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

/** Intentionally invalid: the cache holds TreeType values, not ints. */
public final class FlyWrongValueBroken {
  public static void main(String[] args) {
    Map<String, TreeType> cache = new HashMap<>();
    cache.put("oak", 5);
  }
}
