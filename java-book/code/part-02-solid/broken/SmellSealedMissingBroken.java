package solid.smells;

import solid.smells.switchtype.SwitchTypeDemo.Card;
import solid.smells.switchtype.SwitchTypeDemo.Cash;
import solid.smells.switchtype.SwitchTypeDemo.Payment;
import solid.smells.switchtype.SwitchTypeDemo.Upi;

/** A switch over a sealed type with no default must name every permitted subtype. */
public final class SmellSealedMissingBroken {
  static int fee(Payment p) {
    return switch (p) {
      case Card c -> c.amount() * 2 / 100;
      case Upi u -> u.amount() / 100;
      case Cash c -> 0;
    };
  }
}
