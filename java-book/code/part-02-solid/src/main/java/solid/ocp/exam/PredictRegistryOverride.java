package solid.ocp.exam;

// ANCHOR: predict-registry-override
import solid.ocp.rules.PercentDiscount;
import solid.ocp.step3.DiscountRegistry;

public final class PredictRegistryOverride {
  public static void main(String[] args) {
    DiscountRegistry r = DiscountRegistry.withDefaults();
    r.register("VIP", new PercentDiscount(20));
    System.out.println(r.discount("VIP", 1000));
    System.out.println(r.size());
    r.register("GOLD", new PercentDiscount(25));
    System.out.println(r.size());
  }
}
// ANCHOR_END: predict-registry-override
