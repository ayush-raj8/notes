package solid.ocp.exam;

// ANCHOR: predict-sealed-fee
import solid.ocp.sealed.Payment;
import solid.ocp.sealed.PaymentFee;

public final class PredictSealedFee {
  public static void main(String[] args) {
    System.out.println(PaymentFee.fee(new Payment.Card(10_000)));
    System.out.println(PaymentFee.fee(new Payment.Cash(10_000)));
    System.out.println(PaymentFee.fee(new Payment.Wallet(10_000)));
  }
}
// ANCHOR_END: predict-sealed-fee
