import lld.bookmyshow.PaymentGateway;

public class BmsCheckedLambdaBroken {
  static PaymentGateway slow() {
    return (user, paise) -> {
      Thread.sleep(100);
      return true;
    };
  }
}
