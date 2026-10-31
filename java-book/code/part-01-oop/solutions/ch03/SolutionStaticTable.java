package oop.init.solutions;

import java.util.HashMap;
import java.util.Map;

/** Exercise 3: fill a lookup table once in a static block. */
public final class SolutionStaticTable {
  private static final Map<String, Integer> CODES = new HashMap<>();

  static {
    CODES.put("A", 1);
    CODES.put("B", 2);
    CODES.put("C", 3);
    System.out.println("table built");
  }

  public static int code(String key) {
    return CODES.getOrDefault(key, -1);
  }

  public static void main(String[] args) {
    System.out.println(code("B"));
    System.out.println(code("Z"));
  }
}
