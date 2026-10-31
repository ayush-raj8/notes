package oop.nested;

import java.util.List;

// ANCHOR: local-record
/** Java 16+: records, enums and interfaces may be local. They are implicitly static. */
public final class LocalRecordDemo {
  static String top(List<String> words) {
    record Pair(String word, int len) {}
    Pair best = new Pair("", -1);
    for (String w : words) {
      if (w.length() > best.len()) {
        best = new Pair(w, w.length());
      }
    }
    return best.word() + ":" + best.len();
  }

  public static void main(String[] args) {
    System.out.println(top(List.of("ab", "abcd", "abc")));
  }
}
// ANCHOR_END: local-record
