package lld.atm;

// ANCHOR: insufficient-funds
public class InsufficientFundsException extends Exception {
  public InsufficientFundsException(long balance, long requested) {
    super("balance " + balance + " < requested " + requested);
  }
}
// ANCHOR_END: insufficient-funds
