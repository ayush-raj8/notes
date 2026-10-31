package oop.conc.exam;

// ANCHOR: predict-interrupt-flag
public final class PredictInterruptFlag {
  public static void main(String[] args) {
    Thread me = Thread.currentThread();
    me.interrupt();
    System.out.println(me.isInterrupted());
    System.out.println(me.isInterrupted());
    System.out.println(Thread.interrupted());
    System.out.println(Thread.interrupted());
    me.interrupt();
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      System.out.println("sleep threw; flag=" + me.isInterrupted());
    }
  }
}
// ANCHOR_END: predict-interrupt-flag
