package oop.conc;

import java.util.concurrent.CountDownLatch;

// ANCHOR: thread-states
/** Thread.State: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED. */
public final class ThreadStates {
  public static void main(String[] args) throws Exception {
    Object monitor = new Object();
    CountDownLatch gate = new CountDownLatch(1);
    Thread waiting = new Thread(() -> {
      synchronized (monitor) {
        try {
          monitor.wait();
        } catch (InterruptedException ignored) {
        }
      }
    });
    Thread timed = new Thread(() -> {
      try {
        gate.await();
        Thread.sleep(5_000);
      } catch (InterruptedException ignored) {
      }
    });
    System.out.println("before start:  " + waiting.getState());
    waiting.start();
    timed.start();
    while (waiting.getState() != Thread.State.WAITING) Thread.onSpinWait();
    System.out.println("in wait():     " + waiting.getState());
    gate.countDown();
    while (timed.getState() != Thread.State.TIMED_WAITING) Thread.onSpinWait();
    System.out.println("in sleep():    " + timed.getState());
    waiting.interrupt();
    timed.interrupt();
    waiting.join();
    timed.join();
    System.out.println("after join:    " + waiting.getState());
  }
}
// ANCHOR_END: thread-states
