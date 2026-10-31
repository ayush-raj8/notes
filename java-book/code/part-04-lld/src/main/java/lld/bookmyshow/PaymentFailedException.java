package lld.bookmyshow;

public class PaymentFailedException extends RuntimeException {
  public PaymentFailedException(String userId, long amountPaise) {
    super("payment declined for " + userId + ": " + amountPaise);
  }
}
