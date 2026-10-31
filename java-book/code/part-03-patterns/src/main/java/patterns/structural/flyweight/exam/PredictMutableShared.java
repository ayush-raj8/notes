package patterns.structural.flyweight.exam;

import java.util.HashMap;
import java.util.Map;

// ANCHOR: predict-mutable-shared
public final class PredictMutableShared {
  static final class Pen {
    int width;

    Pen(int width) {
      this.width = width;
    }
  }

  static final Map<String, Pen> POOL = new HashMap<>();

  static Pen pen(String key) {
    return POOL.computeIfAbsent(key, k -> new Pen(1));
  }

  public static void main(String[] args) {
    Pen a = pen("thin");
    Pen b = pen("thin");
    Pen c = pen("bold");
    a.width = 5;
    System.out.println(b.width);
    System.out.println(c.width);
    System.out.println(POOL.size());
  }
}
// ANCHOR_END: predict-mutable-shared
