package solid.ocp.gotchas;

import java.util.HashMap;
import java.util.Map;
import solid.ocp.before.DiscountBefore;
import solid.ocp.rules.Discount;
import solid.ocp.rules.PercentDiscount;
import solid.ocp.sealed.Payment;

public final class OcpGotchas {
  // ANCHOR: g2-instanceof
  record Circle(double r) {}

  record Square(double s) {}

  record Triangle(double b, double h) {}

  /** An instanceof chain is the same violation as the string chain. */
  static double area(Object shape) {
    if (shape instanceof Circle c) {
      return Math.PI * c.r() * c.r();
    } else if (shape instanceof Square s) {
      return s.s() * s.s();
    }
    return 0; // Triangle falls through silently
  }
  // ANCHOR_END: g2-instanceof

  // ANCHOR: g3-static-registry
  /** A static registry is shared by every caller and every test. */
  static final Map<String, Discount> SHARED = new HashMap<>();
  // ANCHOR_END: g3-static-registry

  // ANCHOR: g4-single-impl
  /** An interface with one implementation and no expected variation. */
  interface Rounding {
    long round(long cents);
  }

  static final class WholeUnits implements Rounding {
    public long round(long cents) {
      return cents / 100 * 100;
    }
  }
  // ANCHOR_END: g4-single-impl

  // ANCHOR: g5-default
  /** A default branch switches the exhaustiveness check off. */
  static long feeWithDefault(Payment p) {
    return switch (p) {
      case Payment.Card c -> c.cents() * 2 / 100;
      default -> 0;
    };
  }
  // ANCHOR_END: g5-default

  public static void main(String[] args) {
    // ANCHOR: g-main
    System.out.println("G1 " + DiscountBefore.discount("Vip", 1000));
    System.out.println("G2 " + area(new Triangle(3, 4)));
    SHARED.put("A", new PercentDiscount(10)); // "test one"
    System.out.println("G3 " + SHARED.keySet()); // "test two" sees test one's data
    System.out.println("G4 " + new WholeUnits().round(1999));
    System.out.println("G5 " + feeWithDefault(new Payment.Wallet(10_000)));
    // ANCHOR_END: g-main
  }
}
