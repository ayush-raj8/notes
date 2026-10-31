package oop.immut.exam;

import java.util.EnumMap;
import java.util.Map;

// ANCHOR: predict-enum-map-order
public final class PredictEnumMapOrder {
  enum Color { RED, GREEN, BLUE }

  public static void main(String[] args) {
    Map<Color, Integer> m = new EnumMap<>(Color.class);
    m.put(Color.BLUE, 3);
    m.put(Color.RED, 1);
    m.put(Color.GREEN, 2);
    System.out.println(m.keySet());
    System.out.println(m.values());
  }
}
// ANCHOR_END: predict-enum-map-order
