package oop.coll;

import java.util.HashMap;
import java.util.Map;

public final class CollMapPutWrongTypeBroken {
  public static void main(String[] args) {
    Map<String, Integer> ages = new HashMap<>();
    ages.put("asha", "twelve");
  }
}
