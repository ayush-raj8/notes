package solid.principles.yagni;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class YagniDemo {
  // ANCHOR: yagni-before
  /** Built for "future formats", retries and async that no caller uses. */
  public static final class Before {
    public enum Format {
      CSV,
      JSON,
      XML,
      YAML
    }

    private final Map<Format, Function<List<String>, String>> plugins = new HashMap<>();
    private final int retries;
    private final boolean async;

    public Before(int retries, boolean async) {
      this.retries = retries;
      this.async = async;
      plugins.put(Format.CSV, rows -> String.join(",", rows));
      plugins.put(Format.JSON, rows -> {
        throw new UnsupportedOperationException("JSON not implemented yet");
      });
    }

    public String export(Format f, List<String> rows) {
      Function<List<String>, String> p = plugins.get(f);
      if (p == null) {
        throw new IllegalArgumentException("no plugin for " + f);
      }
      return p.apply(rows);
    }
  }
  // ANCHOR_END: yagni-before

  // ANCHOR: yagni-after
  /** The one thing a caller needs today. */
  public static String csv(List<String> rows) {
    return rows.stream().collect(Collectors.joining(","));
  }
  // ANCHOR_END: yagni-after

  public static void main(String[] args) {
    // ANCHOR: yagni-main
    List<String> rows = List.of("a", "b", "c");
    Before before = new Before(3, false);
    System.out.println("before csv=" + before.export(Before.Format.CSV, rows));
    for (Before.Format f : new Before.Format[] {Before.Format.JSON, Before.Format.XML}) {
      try {
        before.export(f, rows);
      } catch (RuntimeException e) {
        System.out.println("before " + f + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
      }
    }
    System.out.println("after  csv=" + csv(rows));
    // ANCHOR_END: yagni-main
  }
}
