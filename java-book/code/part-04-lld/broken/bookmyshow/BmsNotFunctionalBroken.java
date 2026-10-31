public class BmsNotFunctionalBroken {
  interface Gateway {
    boolean charge(String userId, long paise);

    void refund(String userId, long paise);
  }

  static Gateway approve() {
    return (user, paise) -> true;
  }
}
