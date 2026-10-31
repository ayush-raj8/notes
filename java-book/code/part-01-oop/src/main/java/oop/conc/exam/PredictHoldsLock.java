package oop.conc.exam;

// ANCHOR: predict-holds-lock
public final class PredictHoldsLock {
  synchronized void instance() {
    System.out.println(Thread.holdsLock(this) + " " + Thread.holdsLock(PredictHoldsLock.class));
  }

  static synchronized void statik() {
    System.out.println(Thread.holdsLock(PredictHoldsLock.class));
  }

  public static void main(String[] args) {
    new PredictHoldsLock().instance();
    statik();
    System.out.println(Thread.holdsLock(PredictHoldsLock.class));
  }
}
// ANCHOR_END: predict-holds-lock
