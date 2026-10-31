package oop.conc;

import java.util.concurrent.atomic.AtomicInteger;

// ANCHOR: race-counter
/** count++ is read, add, write: three steps. Two threads can interleave and lose updates. */
public final class RaceCounter {
  static int plain; // shared, unguarded
  static final AtomicInteger atomic = new AtomicInteger();
  static final int PER_THREAD = 200_000;

  static void hammer(Runnable step) throws InterruptedException {
    Thread a = new Thread(() -> { for (int i = 0; i < PER_THREAD; i++) step.run(); });
    Thread b = new Thread(() -> { for (int i = 0; i < PER_THREAD; i++) step.run(); });
    a.start();
    b.start();
    a.join();
    b.join();
  }

  public static void main(String[] args) throws InterruptedException {
    hammer(() -> plain++);
    hammer(atomic::incrementAndGet);
    int expected = 2 * PER_THREAD;
    System.out.println("plain <= expected: " + (plain <= expected)); // never more; usually less
    System.out.println("atomic == expected: " + (atomic.get() == expected));
  }
}
// ANCHOR_END: race-counter
