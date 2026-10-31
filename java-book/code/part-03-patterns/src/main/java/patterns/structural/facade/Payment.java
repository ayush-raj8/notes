package patterns.structural.facade;

// ANCHOR: facade-payment
public class Payment {
  public void charge(int cents) throws PaymentDeclinedException {
    if (cents > 10_000) {
      throw new PaymentDeclinedException("limit exceeded: " + cents);
    }
  }
}
// ANCHOR_END: facade-payment
