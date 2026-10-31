package oop.conc;

/** Intentionally invalid: Thread.run() is public; an override cannot be package-private. */
public final class ConcRunWeakerAccessBroken {
  static class Job extends Thread {
    @Override
    void run() {
      System.out.println("job");
    }
  }
}
