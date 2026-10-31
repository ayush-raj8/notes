package solid.ocp.exam;

// ANCHOR: compile-ok-lambda
import solid.ocp.rules.Discount;
import solid.ocp.step3.DiscountRegistry;

public final class CompileLambdaDiscountOk {
  public static void main(String[] args) {
    Discount half = cents -> cents / 2;
    DiscountRegistry r = new DiscountRegistry();
    r.register("HALF", half);
    System.out.println(r.discount("HALF", 300));
  }
}
// ANCHOR_END: compile-ok-lambda
