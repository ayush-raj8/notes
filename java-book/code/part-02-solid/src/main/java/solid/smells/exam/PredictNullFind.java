package solid.smells.exam;

// ANCHOR: predict-null-find
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PredictNullFind {
  static Optional<String> find(Map<String, String> m, String k) {
    return Optional.ofNullable(m.get(k));
  }

  public static void main(String[] args) {
    Map<String, String> m = Map.of("a", "x");
    System.out.println(find(m, "a"));
    System.out.println(find(m, "b"));
    System.out.println(find(m, "b").isPresent());
    System.out.println(find(m, "b").map(String::length).orElse(-1));
    List<String> none = List.of();
    for (String s : none) {
      System.out.println("never printed " + s);
    }
    System.out.println(none.size());
    try {
      System.out.println(find(m, "b").get());
    } catch (java.util.NoSuchElementException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: predict-null-find
