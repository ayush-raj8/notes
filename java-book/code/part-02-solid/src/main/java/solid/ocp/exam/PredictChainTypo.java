package solid.ocp.exam;

// ANCHOR: predict-chain-typo
import solid.ocp.before.DiscountBefore;

public final class PredictChainTypo {
  public static void main(String[] args) {
    System.out.println(DiscountBefore.discount("VIP", 1000));
    System.out.println(DiscountBefore.discount("vip", 1000));
    try {
      System.out.println(DiscountBefore.discount(null, 1000));
    } catch (NullPointerException e) {
      System.out.println("NPE");
    }
  }
}
// ANCHOR_END: predict-chain-typo
