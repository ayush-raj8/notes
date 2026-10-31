package oop.conc.solutions;

import java.util.concurrent.atomic.LongAdder;

/** Exercise 1: four threads, 100_000 increments each, exact total with no synchronized keyword. */
public final class SolutionSafeCounter {
  public static void main(String[] args) throws InterruptedException {
    LongAdder total = new LongAdder();
    Thread[] ts = new Thread[4];
    for (int i = 0; i < ts.length; i++) {
      ts[i] = new Thread(() -> {
        for (int k = 0; k < 100_000; k++) total.increment();
      });
      ts[i].start();
    }
    for (Thread t : ts) t.join();
    System.out.println("total=" + total.sum());
  }
}
