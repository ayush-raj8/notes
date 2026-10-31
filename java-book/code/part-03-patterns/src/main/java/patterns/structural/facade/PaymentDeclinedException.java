package patterns.structural.facade;

public class PaymentDeclinedException extends Exception {
  public PaymentDeclinedException(String message) {
    super(message);
  }
}
