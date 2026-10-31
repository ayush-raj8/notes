package oop.conc;

/** Intentionally invalid: Runnable.run() cannot throw a checked exception. */
public final class ConcRunnableCheckedBroken {
  public static void main(String[] args) {
    Thread t = new Thread(() -> {
      Thread.sleep(10);
    });
    t.start();
  }
}
