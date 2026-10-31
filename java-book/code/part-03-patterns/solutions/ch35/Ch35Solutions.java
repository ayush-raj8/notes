package patterns.structural.facade.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import patterns.structural.facade.Inventory;
import patterns.structural.facade.OrderFacade;
import patterns.structural.facade.Payment;
import patterns.structural.facade.Shipping;

public final class Ch35Solutions {
  // ANCHOR: ch35-ex1
  /** Ex1: facade over java.util.Properties parsing: one call, no checked exceptions. */
  static final class Settings {
    private final Properties props = new Properties();

    Settings(String text) {
      try {
        props.load(new java.io.StringReader(text));
      } catch (java.io.IOException e) {
        throw new java.io.UncheckedIOException(e);
      }
    }

    int intOr(String key, int fallback) {
      String v = props.getProperty(key);
      return v == null ? fallback : Integer.parseInt(v.trim());
    }
  }
  // ANCHOR_END: ch35-ex1

  // ANCHOR: ch35-ex2
  /** Ex2: facade that places several orders and reports a summary. */
  static String bulk(OrderFacade f, Map<String, Integer> lines) {
    List<String> out = new ArrayList<>();
    lines.forEach((sku, cents) -> out.add(sku + "=" + f.place(sku, cents).substring(0, 2)));
    out.sort(String::compareTo);
    return String.join(", ", out);
  }
  // ANCHOR_END: ch35-ex2

  // ANCHOR: ch35-ex3
  /** Ex3: facade with validation before touching the subsystem. */
  static final class SafeOrders {
    private final OrderFacade inner;

    SafeOrders(OrderFacade inner) {
      this.inner = inner;
    }

    String place(String sku, int cents) {
      if (sku == null || sku.isBlank() || cents <= 0) {
        return "REJECTED";
      }
      return inner.place(sku, cents);
    }
  }
  // ANCHOR_END: ch35-ex3

  // ANCHOR: ch35-ex4
  /** Ex4: simple text facade over several String operations. */
  static String slug(String title) {
    return title.strip().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
  }
  // ANCHOR_END: ch35-ex4

  // ANCHOR: ch35-ex5
  /** Ex5: why a facade is not enough to enforce a rule: a public subsystem can be bypassed. */
  static int bypassLeavesNoOrder() {
    Inventory inv = new Inventory();
    inv.reserve("pen");
    return inv.left("pen");
  }
  // ANCHOR_END: ch35-ex5

  public static void main(String[] args) {
    Settings s = new Settings("port = 8080\n");
    System.out.println("ex1 " + s.intOr("port", 1) + " " + s.intOr("missing", 7));
    OrderFacade f = new OrderFacade(new Inventory(), new Payment(), new Shipping());
    System.out.println("ex2 " + bulk(f, Map.of("pen", 100, "ink", 99_999)));
    SafeOrders so = new SafeOrders(f);
    System.out.println("ex3 " + so.place(" ", 5) + " " + so.place("ink", 5));
    System.out.println("ex4 " + slug("  Hello, World! 2026 "));
    System.out.println("ex5 " + bypassLeavesNoOrder());
  }
}
