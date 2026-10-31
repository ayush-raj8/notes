package solid.ocp.after;

// ANCHOR: after-demo
import solid.ocp.rules.CappedDiscount;
import solid.ocp.step3.DiscountRegistry;

public final class OcpAfterDemo {
  public static void main(String[] args) {
    DiscountRegistry registry = DiscountRegistry.withDefaults();
    registry.register("EMPLOYEE", new CappedDiscount(30, 5_000));
    registry.register("PARTNER", cents -> cents / 4);

    for (String type : new String[] {"VIP", "EMPLOYEE", "PARTNER"}) {
      System.out.println(type + " discount=" + registry.discount(type, 20_000));
    }
    try {
      registry.discount("GUEST", 20_000);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: after-demo
