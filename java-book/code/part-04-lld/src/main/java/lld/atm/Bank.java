package lld.atm;

// ANCHOR: bank
/** Port to the bank back end. The ATM never sees accounts, only this interface. */
public interface Bank {
  boolean verifyPin(String card, String pin);

  long balance(String card);

  void debit(String card, long amount) throws InsufficientFundsException;

  void credit(String card, long amount);
}
// ANCHOR_END: bank
