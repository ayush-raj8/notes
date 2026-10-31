package patterns.behavioral.observer;

public final class SelfRemoveDemo {
  // ANCHOR: self-remove
  public static void main(String[] args) {
    NaivePriceSubject naive = new NaivePriceSubject();
    PriceObserver[] once = new PriceObserver[1];
    once[0] =
        (s, p) -> {
          System.out.println("first");
          naive.detach(once[0]);
        };
    naive.attach(once[0]);
    naive.attach((s, p) -> System.out.println("second"));
    naive.attach((s, p) -> System.out.println("third"));
    try {
      naive.publish("X", 1);
    } catch (java.util.ConcurrentModificationException e) {
      System.out.println("CME");
    }

    PriceSubject safe = new PriceSubject();
    PriceObserver[] once2 = new PriceObserver[1];
    once2[0] =
        (s, p) -> {
          System.out.println("first");
          safe.detach(once2[0]);
        };
    safe.attach(once2[0]);
    safe.attach((s, p) -> System.out.println("second"));
    safe.attach((s, p) -> System.out.println("third"));
    safe.publish("X", 1);
  }
  // ANCHOR_END: self-remove
}
