package solid.ocp.exam;

// ANCHOR: predict-capped
import solid.ocp.rules.CappedDiscount;
import solid.ocp.rules.Discount;

public final class PredictCapped {
  public static void main(String[] args) {
    Discount d = new CappedDiscount(30, 5_000);
    System.out.println(d.amount(20_000));
    System.out.println(d.amount(10_000));
    System.out.println(d);
  }
}
// ANCHOR_END: predict-capped
