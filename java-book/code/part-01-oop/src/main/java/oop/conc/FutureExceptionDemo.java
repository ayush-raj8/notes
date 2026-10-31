package oop.conc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// ANCHOR: future-exception
/** An exception thrown inside submit(...) is captured; Future.get() rethrows it wrapped in ExecutionException. */
public final class FutureExceptionDemo {
  public static void main(String[] args) throws InterruptedException {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    Future<Integer> f = pool.submit(() -> Integer.parseInt("x1"));
    try {
      f.get();
    } catch (ExecutionException e) {
      System.out.println("wrapper: " + e.getClass().getSimpleName());
      System.out.println("cause:   " + e.getCause().getClass().getSimpleName());
    }
    System.out.println("isDone: " + f.isDone());
    pool.shutdown();
  }
}
// ANCHOR_END: future-exception
