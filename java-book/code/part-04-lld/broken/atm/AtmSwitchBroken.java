import lld.atm.AtmState;

public class AtmSwitchBroken {
  static String label(AtmState s) {
    return switch (s) {
      case IDLE -> "waiting";
      case CARD_INSERTED -> "need pin";
    };
  }

  public static void main(String[] args) {
    System.out.println(label(AtmState.IDLE));
  }
}
