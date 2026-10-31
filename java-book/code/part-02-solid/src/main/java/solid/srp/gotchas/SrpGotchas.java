package solid.srp.gotchas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import solid.srp.before.InvoiceGod;
import solid.srp.model.Line;

public final class SrpGotchas {
  // ANCHOR: g1-cohesive
  /** Three methods, one reason to change (money arithmetic): this class is fine. */
  record Money(long cents) {
    Money plus(Money o) {
      return new Money(cents + o.cents);
    }

    Money minus(Money o) {
      return new Money(cents - o.cents);
    }

    String text() {
      return cents / 100 + "." + String.format("%02d", cents % 100);
    }
  }
  // ANCHOR_END: g1-cohesive

  // ANCHOR: g2-util
  /** A static grab-bag still has several reasons to change, and cannot be replaced in a test. */
  static final class InvoiceUtils {
    static long total(List<Line> lines) {
      return lines.stream().mapToLong(Line::cents).sum();
    }

    static String text(String id, long cents) {
      return id + " total=" + Math.round(cents / 100.0);
    }

    static void save(Map<String, Long> db, String id, long cents) {
      db.put(id, cents);
    }
  }
  // ANCHOR_END: g2-util

  // ANCHOR: g3-oversplit
  /** Three one-line classes that are always called together: splitting past the reasons to change. */
  static final class Add {
    long apply(long a, long b) {
      return a + b;
    }
  }

  static final class Tax {
    long apply(long a) {
      return a + a / 10;
    }
  }

  static final class Round {
    long apply(long a) {
      return (a / 100) * 100;
    }
  }
  // ANCHOR_END: g3-oversplit

  public static void main(String[] args) {
    // ANCHOR: g-main
    System.out.println("G1 " + new Money(1050).plus(new Money(295)).text());

    Map<String, Long> db = new HashMap<>();
    InvoiceUtils.save(db, "X", InvoiceUtils.total(List.of(new Line("A", 1, 250))));
    System.out.println("G2 " + InvoiceUtils.text("X", db.get("X")));

    System.out.println("G3 " + new Round().apply(new Tax().apply(new Add().apply(450, 600))));

    InvoiceGod god = new InvoiceGod("G");
    god.add(new Line("A", 1, 250));
    Map<String, Long> late = new HashMap<>();
    god.save(late); // save() called before total()
    System.out.println("G4 stored=" + late.get("G"));

    System.out.println("G5 " + new Line("A", 2, 4999));
    // ANCHOR_END: g-main
  }
}
