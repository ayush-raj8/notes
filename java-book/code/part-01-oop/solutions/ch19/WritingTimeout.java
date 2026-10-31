package oop.conc.solutions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Writing prompt 3: run a slow task, wait at most 50 ms, cancel it on timeout. */
public final class WritingTimeout {
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    Future<String> f = pool.submit(() -> {
      Thread.sleep(5_000);
      return "late";
    });
    try {
      System.out.println(f.get(50, TimeUnit.MILLISECONDS));
    } catch (TimeoutException e) {
      boolean cancelled = f.cancel(true); // interrupts the sleeping task
      System.out.println("timed out, cancelled=" + cancelled + ", isCancelled=" + f.isCancelled());
    } finally {
      pool.shutdown();
    }
  }
}
