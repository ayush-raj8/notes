package solid.ocp.exam;

// ANCHOR: predict-registry-unknown
import solid.ocp.step3.DiscountRegistry;

public final class PredictRegistryUnknown {
  public static void main(String[] args) {
    DiscountRegistry r = DiscountRegistry.withDefaults();
    System.out.println(r.discount("VIP", 99));
    try {
      System.out.println(r.discount("vip", 99));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: predict-registry-unknown
