package oop.conc.exam;

// ANCHOR: predict-run-vs-start
public final class PredictRunVsStart {
  public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread(() -> System.out.println(Thread.currentThread().getName()), "worker");
    t.run();
    t.start();
    t.join();
    System.out.println("done");
  }
}
// ANCHOR_END: predict-run-vs-start
