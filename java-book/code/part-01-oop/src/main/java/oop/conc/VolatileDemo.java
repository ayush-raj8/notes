package oop.conc;

import java.util.concurrent.atomic.AtomicInteger;

// ANCHOR: volatile-demo
/** volatile: visibility + ordering for one variable. It does not make count++ atomic. */
public final class VolatileDemo {
  private static volatile boolean running = true; // without volatile the loop may never see false
  private static volatile int vcount; // volatile, but ++ is still three steps
  private static final AtomicInteger acount = new AtomicInteger();

  public static void main(String[] args) throws InterruptedException {
    Thread spinner = new Thread(() -> {
      long spins = 0;
      while (running) {
        spins++;
      }
      System.out.println("spinner saw running=false, spun>0: " + (spins > 0));
    });
    spinner.start();
    Thread.sleep(50);
    running = false; // write is visible to the spinner
    spinner.join();

    Runnable both = () -> {
      for (int i = 0; i < 100_000; i++) {
        vcount++;
        acount.incrementAndGet();
      }
    };
    Thread a = new Thread(both);
    Thread b = new Thread(both);
    a.start(); b.start(); a.join(); b.join();
    System.out.println("volatile <= 200000: " + (vcount <= 200_000)); // never more; usually less
    System.out.println("atomic   == 200000: " + (acount.get() == 200_000));
  }
}
// ANCHOR_END: volatile-demo
