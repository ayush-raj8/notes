package patterns.behavioral.observer;

public final class ObserverDemo {
  // ANCHOR: observer-demo
  public static void main(String[] args) {
    PriceSubject subject = new PriceSubject();
    PriceObserver logger = (sym, p) -> System.out.println("log " + sym + "=" + p);
    PriceObserver alert =
        (sym, p) -> {
          if (p > 100) System.out.println("ALERT " + sym);
        };
    subject.attach(logger);
    subject.attach(alert);
    subject.publish("ACME", 90);
    subject.publish("ACME", 120);
    subject.detach(alert);
    subject.publish("ACME", 130);
  }
  // ANCHOR_END: observer-demo
}
