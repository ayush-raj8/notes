package oop.conc;

/** Intentionally invalid: the monitor of synchronized(...) must be a reference. */
public final class ConcSynchronizedPrimitiveBroken {
  private int lock;

  void run() {
    synchronized (lock) {
      lock++;
    }
  }
}
