package patterns.structural.flyweight.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Ch39Solutions {
  // ANCHOR: ch39-ex1
  /** Ex1: bullet flyweight: sprite is intrinsic and shared; position and velocity are extrinsic. */
  record Sprite(String name) {}

  static final class SpriteFactory {
    private static final Map<String, Sprite> CACHE = new HashMap<>();

    static Sprite get(String name) {
      return CACHE.computeIfAbsent(name, Sprite::new);
    }

    static int size() {
      return CACHE.size();
    }
  }

  record Bullet(int x, int y, Sprite sprite) {
    Bullet moved(int dx, int dy) {
      return new Bullet(x + dx, y + dy, sprite);
    }
  }
  // ANCHOR_END: ch39-ex1

  // ANCHOR: ch39-ex2
  /** Ex2: generic interner: return the first equal instance seen. */
  static final class Interner<T> {
    private final Map<T, T> pool = new HashMap<>();

    T intern(T value) {
      return pool.computeIfAbsent(value, v -> v);
    }

    int size() {
      return pool.size();
    }
  }
  // ANCHOR_END: ch39-ex2

  // ANCHOR: ch39-ex3
  /** Ex3: split state: classify each field of a map marker as intrinsic or extrinsic. */
  static String classify(String field) {
    return switch (field) {
      case "icon", "color" -> "intrinsic (shared)";
      case "lat", "lng", "label" -> "extrinsic (per marker)";
      default -> "unknown";
    };
  }
  // ANCHOR_END: ch39-ex3

  // ANCHOR: ch39-ex4
  /** Ex4: count objects created for n markers with and without sharing. */
  static String objects(int markers, int distinctIcons) {
    return (markers * 2) + " without sharing, " + (markers + distinctIcons) + " with sharing";
  }
  // ANCHOR_END: ch39-ex4

  // ANCHOR: ch39-ex5
  /** Ex5: why Integer.valueOf(1000) == Integer.valueOf(1000) is false: only -128..127 are cached. */
  static boolean sameBox(int v) {
    return Integer.valueOf(v) == Integer.valueOf(v);
  }
  // ANCHOR_END: ch39-ex5

  public static void main(String[] args) {
    Bullet b = new Bullet(0, 0, SpriteFactory.get("round"));
    Bullet c = b.moved(2, 3);
    System.out.println("ex1 " + c + " " + (b.sprite() == c.sprite()) + " " + SpriteFactory.size());
    Interner<String> in = new Interner<>();
    String first = in.intern(new String("k"));
    String second = in.intern(new String("k"));
    System.out.println("ex2 " + (first == second) + " " + in.size());
    List<String> out = new ArrayList<>();
    for (String f : List.of("icon", "lat", "label", "zoom")) {
      out.add(f + "=" + classify(f));
    }
    System.out.println("ex3 " + out);
    System.out.println("ex4 " + objects(1000, 3));
    System.out.println("ex5 " + sameBox(100) + " " + sameBox(1000));
  }
}
