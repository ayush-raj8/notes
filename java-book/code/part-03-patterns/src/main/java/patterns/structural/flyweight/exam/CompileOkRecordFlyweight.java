package patterns.structural.flyweight.exam;

import java.util.HashMap;
import java.util.Map;

// ANCHOR: compile-ok-record-flyweight
/** Compiles: a record is immutable and has value equals, so it works as flyweight and as map key. */
public final class CompileOkRecordFlyweight {
  record Color(int r, int g, int b) {
    Color {
      if (r < 0 || g < 0 || b < 0) {
        throw new IllegalArgumentException("negative channel");
      }
    }
  }

  static final Map<Color, Color> CACHE = new HashMap<>();

  static Color of(int r, int g, int b) {
    return CACHE.computeIfAbsent(new Color(r, g, b), k -> k);
  }

  public static void main(String[] args) {
    System.out.println(of(1, 2, 3) == of(1, 2, 3));
    System.out.println(CACHE.size());
  }
}
// ANCHOR_END: compile-ok-record-flyweight
