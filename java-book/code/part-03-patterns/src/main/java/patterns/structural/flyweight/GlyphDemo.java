package patterns.structural.flyweight;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// ANCHOR: glyph-demo
/** Text editor: one Font per (family,size); each character cell stores only its char and a Font reference. */
public final class GlyphDemo {
  record Font(String family, int size) {}

  static final ConcurrentMap<Font, Font> FONTS = new ConcurrentHashMap<>();

  static Font font(String family, int size) {
    Font key = new Font(family, size);
    return FONTS.computeIfAbsent(key, k -> k);
  }

  record Glyph(char ch, Font font) {}

  public static void main(String[] args) {
    Glyph[] text = new Glyph[5];
    String s = "hello";
    for (int i = 0; i < s.length(); i++) {
      text[i] = new Glyph(s.charAt(i), font("Serif", 12));
    }
    System.out.println(text[0].font() == text[4].font());
    System.out.println(FONTS.size());
    System.out.println(font("Serif", 14) == text[0].font());
    System.out.println(FONTS.size());
  }
}
// ANCHOR_END: glyph-demo
