package oop.conc;

// ANCHOR: sync-counter
/** synchronized gives mutual exclusion and visibility. Same lock = the same object (here: this). */
public final class SyncCounter {
  private int n;
  private final Object guard = new Object();
  private static int total;

  public synchronized void inc() { // lock = this
    n++;
  }

  public void incBlock() {
    synchronized (guard) { // lock = guard; keeps the critical section small
      n++;
    }
  }

  public static synchronized void incTotal() { // lock = SyncCounter.class
    total++;
  }

  public synchronized int get() {
    return n;
  }

  public static void main(String[] args) throws InterruptedException {
    SyncCounter c = new SyncCounter();
    Thread a = new Thread(() -> { for (int i = 0; i < 100_000; i++) c.inc(); });
    Thread b = new Thread(() -> { for (int i = 0; i < 100_000; i++) c.inc(); });
    a.start(); b.start(); a.join(); b.join();
    System.out.println("n=" + c.get());
    incTotal();
    System.out.println("total=" + total);
    c.probe();
  }

  /** Different locks: the method lock (this) and the block lock (guard) do not exclude each other. */
  public synchronized void probe() {
    System.out.println("holds this: " + Thread.holdsLock(this) + ", holds guard: " + Thread.holdsLock(guard));
    synchronized (guard) {
      System.out.println("now holds guard: " + Thread.holdsLock(guard));
    }
  }
}
// ANCHOR_END: sync-counter
