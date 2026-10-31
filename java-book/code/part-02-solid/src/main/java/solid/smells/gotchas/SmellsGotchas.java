package solid.smells.gotchas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SmellsGotchas {
  // ANCHOR: g1-extract-local
  /** Extracting a loop that updates a local: the helper changes its own copy. */
  static void addAllBroken(int total, int[] xs) {
    for (int x : xs) {
      total += x;
    }
  }

  static int addAll(int[] xs) {
    int total = 0;
    for (int x : xs) {
      total += x;
    }
    return total;
  }
  // ANCHOR_END: g1-extract-local

  // ANCHOR: g3-switch-statement
  enum Level {
    LOW,
    HIGH,
    CRITICAL // added later
  }

  /** A switch statement may skip a constant without a word; a switch expression may not. */
  static String actionStatement(Level l) {
    String action = "ignore";
    switch (l) {
      case LOW:
        action = "log";
        break;
      case HIGH:
        action = "page";
        break;
    }
    return action;
  }
  // ANCHOR_END: g3-switch-statement

  // ANCHOR: g5-record-alias
  record Basket(String owner, List<String> items) {}
  // ANCHOR_END: g5-record-alias

  public static void main(String[] args) {
    // ANCHOR: g-main
    int[] xs = {1, 2, 3};
    int total = 0;
    addAllBroken(total, xs);
    System.out.println("G1 " + total + " vs " + addAll(xs));

    Optional<String> none = Optional.empty();
    try {
      none.get();
    } catch (java.util.NoSuchElementException e) {
      System.out.println("G2 " + e.getMessage());
    }

    System.out.println("G3 " + actionStatement(Level.HIGH) + " " + actionStatement(Level.CRITICAL));

    System.out.println("G4 " + new BigDecimal("1.0").equals(new BigDecimal("1.00"))
        + " " + (new BigDecimal("1.0").compareTo(new BigDecimal("1.00")) == 0));

    List<String> items = new ArrayList<>(List.of("pen"));
    Basket b = new Basket("Asha", items);
    items.add("ink");
    System.out.println("G5 " + b.items().size() + " " + b);
    // ANCHOR_END: g-main
  }
}
