package solid.smells.solutions;

import java.util.HashMap;
import java.util.Map;

/** Exercise 3: replace save(key, value, overwrite) by create(...) and replace(...). */
public final class SolutionSplitFlag {
  static final class Store {
    private final Map<String, String> data = new HashMap<>();

    boolean create(String key, String value) {
      return data.putIfAbsent(key, value) == null;
    }

    boolean replace(String key, String value) {
      return data.replace(key, value) != null;
    }

    String get(String key) {
      return data.get(key);
    }
  }

  public static void main(String[] args) {
    Store s = new Store();
    System.out.println(s.create("a", "1") + " " + s.create("a", "2") + " " + s.get("a"));
    System.out.println(s.replace("a", "3") + " " + s.replace("b", "9") + " " + s.get("a") + " " + s.get("b"));
  }
}
