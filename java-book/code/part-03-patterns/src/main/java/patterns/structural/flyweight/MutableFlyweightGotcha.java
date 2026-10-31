package patterns.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

// ANCHOR: mutable-flyweight-gotcha
/** A flyweight with setters is shared state: one change shows everywhere. */
public final class MutableFlyweightGotcha {
  static final class Style {
    String color;

    Style(String color) {
      this.color = color;
    }
  }

  static final Map<String, Style> CACHE = new HashMap<>();

  static Style style(String key) {
    return CACHE.computeIfAbsent(key, Style::new);
  }

  public static void main(String[] args) {
    Style a = style("body");
    Style b = style("body");
    a.color = "red";
    System.out.println(b.color);
    System.out.println(a == b);
  }
}
// ANCHOR_END: mutable-flyweight-gotcha
