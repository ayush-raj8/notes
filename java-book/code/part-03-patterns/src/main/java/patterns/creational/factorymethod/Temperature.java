package patterns.creational.factorymethod;

import java.util.HashMap;
import java.util.Map;

// ANCHOR: temperature
/** Static factory methods: named, can cache, can return a subtype. Not the GoF pattern. */
public final class Temperature {
  private static final Map<Integer, Temperature> CACHE = new HashMap<>();
  private final int celsius;

  private Temperature(int celsius) {
    this.celsius = celsius;
  }

  public static Temperature ofCelsius(int c) {
    return CACHE.computeIfAbsent(c, Temperature::new);
  }

  public static Temperature ofFahrenheit(int f) {
    return ofCelsius(Math.round((f - 32) * 5f / 9));
  }

  public static Temperature freezing() {
    return ofCelsius(0);
  }

  public int celsius() {
    return celsius;
  }
}
// ANCHOR_END: temperature
