package solid.isp.gotchas;

import solid.isp.after.AllInOne;
import solid.isp.after.DocScanner;
import solid.isp.after.Printer;
import solid.isp.before.BasicPrinter;

public final class IspGotchas {
  // ANCHOR: g2-default
  /** Default methods that throw only move the fat interface problem into the interface body. */
  interface FatWithDefaults {
    String print(String doc);

    default String scan() {
      throw new UnsupportedOperationException("scan");
    }
  }
  // ANCHOR_END: g2-default

  // ANCHOR: g3-too-fine
  interface HasId {
    String id();
  }

  interface HasName {
    String name();
  }

  /** A client that needs both is now forced into an intersection type. */
  static <T extends HasId & HasName> String label(T t) {
    return t.id() + ":" + t.name();
  }

  record User(String id, String name) implements HasId, HasName {}
  // ANCHOR_END: g3-too-fine

  // ANCHOR: g4-probe
  /** The signature says Object; the body probes roles. The type no longer documents the need. */
  static String archiveIfPossible(Object device) {
    if (device instanceof DocScanner s) {
      return "archived:" + s.scan();
    }
    return "skipped";
  }
  // ANCHOR_END: g4-probe

  // ANCHOR: g5-intersection
  /** Require several small roles at once without a fat interface. */
  static <T extends Printer & DocScanner> String copy(T device) {
    return device.print(device.scan());
  }
  // ANCHOR_END: g5-intersection

  public static void main(String[] args) {
    // ANCHOR: g-main
    System.out.println("G1 fax=" + new BasicPrinter().fax("memo"));

    FatWithDefaults fat = doc -> "printed:" + doc;
    try {
      fat.scan();
    } catch (UnsupportedOperationException e) {
      System.out.println("G2 " + e.getMessage());
    }

    System.out.println("G3 " + label(new User("u1", "Asha")));
    System.out.println("G4 " + archiveIfPossible(new AllInOne()) + " " + archiveIfPossible("x"));
    System.out.println("G5 " + copy(new AllInOne()));
    // ANCHOR_END: g-main
  }
}
