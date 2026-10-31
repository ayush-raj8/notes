package lld.atm;

// ANCHOR: cash-unavailable
public class CashUnavailableException extends Exception {
  public CashUnavailableException(int amount) {
    super("cannot dispense " + amount + " with the notes in the machine");
  }
}
// ANCHOR_END: cash-unavailable
