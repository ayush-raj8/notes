package lld.atm;

// ANCHOR: atm-state
public enum AtmState {
  IDLE, CARD_INSERTED, AUTHENTICATED;

  /** Exhaustive switch expression: adding a state without a case is a compile error. */
  public boolean allows(Operation op) {
    return switch (this) {
      case IDLE -> op == Operation.INSERT_CARD;
      case CARD_INSERTED -> op == Operation.ENTER_PIN || op == Operation.EJECT;
      case AUTHENTICATED -> op != Operation.INSERT_CARD && op != Operation.ENTER_PIN;
    };
  }
}
// ANCHOR_END: atm-state
