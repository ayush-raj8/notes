package oop.ex;

// ANCHOR: custom-exception
public class InsufficientFundsException extends Exception {
  private final int shortBy;

  public InsufficientFundsException(String message, int shortBy) {
    super(message);
    this.shortBy = shortBy;
  }

  public InsufficientFundsException(String message, int shortBy, Throwable cause) {
    super(message, cause);
    this.shortBy = shortBy;
  }

  public int shortBy() {
    return shortBy;
  }
}
// ANCHOR_END: custom-exception
