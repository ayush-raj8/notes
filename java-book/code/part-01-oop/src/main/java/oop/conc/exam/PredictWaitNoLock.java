package oop.conc.exam;

// ANCHOR: predict-wait-no-lock
public final class PredictWaitNoLock {
  public static void main(String[] args) throws InterruptedException {
    Object o = new Object();
    try {
      o.notify();
      System.out.println("notified");
    } catch (IllegalMonitorStateException e) {
      System.out.println("IMSE: " + e.getMessage());
    }
    synchronized (o) {
      o.notify();
      System.out.println("notified inside synchronized");
    }
  }
}
// ANCHOR_END: predict-wait-no-lock
