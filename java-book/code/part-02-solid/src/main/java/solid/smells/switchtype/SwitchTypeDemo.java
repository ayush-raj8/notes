package solid.smells.switchtype;

public final class SwitchTypeDemo {
  // ANCHOR: switch-before
  public enum Kind {
    CARD,
    UPI,
    CASH,
    WALLET // added last week; the switch below was not updated
  }

  public static final class Before {
    public static int fee(Kind kind, int amount) {
      return switch (kind) {
        case CARD -> amount * 2 / 100;
        case UPI -> amount / 100;
        case CASH -> 0;
        default -> 0; // WALLET lands here: a silent zero
      };
    }
  }
  // ANCHOR_END: switch-before

  // ANCHOR: switch-after
  public sealed interface Payment permits Card, Upi, Cash, Wallet {
    int amount();
  }

  public record Card(int amount) implements Payment {}

  public record Upi(int amount) implements Payment {}

  public record Cash(int amount) implements Payment {}

  public record Wallet(int amount) implements Payment {}

  public static final class After {
    /** No default: a fifth Payment makes this a compile error until it is handled. */
    public static int fee(Payment p) {
      return switch (p) {
        case Card c -> c.amount() * 2 / 100;
        case Upi u -> u.amount() / 100;
        case Cash c -> 0;
        case Wallet w -> 50;
      };
    }
  }
  // ANCHOR_END: switch-after

  public static void main(String[] args) {
    // ANCHOR: switch-main
    System.out.println("before CARD   " + Before.fee(Kind.CARD, 1000));
    System.out.println("before WALLET " + Before.fee(Kind.WALLET, 1000));
    System.out.println("after  CARD   " + After.fee(new Card(1000)));
    System.out.println("after  WALLET " + After.fee(new Wallet(1000)));
    // ANCHOR_END: switch-main
  }
}
