package solid.ocp.sealed;

// ANCHOR: payment-fee
import solid.ocp.sealed.Payment.Card;
import solid.ocp.sealed.Payment.Cash;
import solid.ocp.sealed.Payment.Wallet;

/** Java 21 pattern switch, no default: adding a subtype breaks compilation here. */
public final class PaymentFee {
  public static long fee(Payment p) {
    return switch (p) {
      case Card c -> c.cents() * 2 / 100;
      case Cash c -> 0;
      case Wallet w -> w.cents() / 100;
    };
  }
}
// ANCHOR_END: payment-fee
