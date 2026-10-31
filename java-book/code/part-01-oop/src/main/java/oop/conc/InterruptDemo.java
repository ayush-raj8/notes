package oop.conc;

// ANCHOR: interrupt-demo
/** interrupt() sets a flag. Blocking calls (sleep, wait, join) throw InterruptedException and clear it. */
public final class InterruptDemo {
  public static void main(String[] args) throws InterruptedException {
    Thread sleeper = new Thread(() -> {
      try {
        Thread.sleep(10_000);
        System.out.println("not printed");
      } catch (InterruptedException e) {
        System.out.println("woken; flag now: " + Thread.currentThread().isInterrupted());
        Thread.currentThread().interrupt(); // restore the status for callers
        System.out.println("restored; flag now: " + Thread.currentThread().isInterrupted());
      }
    });
    sleeper.start();
    sleeper.interrupt();
    sleeper.join();

    Thread.currentThread().interrupt();
    System.out.println("Thread.interrupted(): " + Thread.interrupted()); // tests and clears
    System.out.println("Thread.interrupted(): " + Thread.interrupted());
  }
}
// ANCHOR_END: interrupt-demo
