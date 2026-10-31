package oop.conc;

// ANCHOR: start-vs-run
/** run() is an ordinary call on the caller's thread. start() runs run() on a new thread, once. */
public final class StartVsRun {
  public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread(() -> System.out.println("body on " + Thread.currentThread().getName()), "worker");
    t.run(); // plain method call
    System.out.println("state after run(): " + t.getState());
    t.start();
    t.join();
    System.out.println("state after join: " + t.getState());
    try {
      t.start(); // a terminated thread cannot be restarted
    } catch (IllegalThreadStateException e) {
      System.out.println("second start: " + e.getClass().getSimpleName());
    }
  }
}
// ANCHOR_END: start-vs-run
