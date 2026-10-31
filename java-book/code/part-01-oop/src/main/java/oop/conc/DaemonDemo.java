package oop.conc;

import java.util.concurrent.CountDownLatch;

// ANCHOR: daemon-demo
/** The JVM exits when only daemon threads remain. setDaemon must be called before start(). */
public final class DaemonDemo {
  public static void main(String[] args) throws InterruptedException {
    CountDownLatch release = new CountDownLatch(1);
    Thread t = new Thread(() -> {
      try {
        release.await();
      } catch (InterruptedException ignored) {
      }
    });
    System.out.println("default daemon: " + t.isDaemon());
    t.setDaemon(true); // allowed: not started yet
    System.out.println("after setDaemon: " + t.isDaemon());
    t.start();
    try {
      t.setDaemon(false); // thread is alive (blocked on the latch)
    } catch (IllegalThreadStateException e) {
      System.out.println("setDaemon after start: IllegalThreadStateException");
    }
    release.countDown();
    t.join();
    System.out.println("main thread daemon: " + Thread.currentThread().isDaemon());
  }
}
// ANCHOR_END: daemon-demo
