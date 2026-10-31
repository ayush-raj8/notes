package solid.ocp;

import solid.ocp.sealed.Payment;
import solid.ocp.sealed.Payment.Card;
import solid.ocp.sealed.Payment.Cash;

/** The switch forgot Wallet: the compiler names the gap. */
public final class OcpSealedMissingCaseBroken {
  static long fee(Payment p) {
    return switch (p) {
      case Card c -> c.cents() * 2 / 100;
      case Cash c -> 0;
    };
  }
}
