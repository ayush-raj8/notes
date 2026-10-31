package oop.conc.exam;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// ANCHOR: predict-execution-exception
public final class PredictExecutionException {
  public static void main(String[] args) throws InterruptedException {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    Future<?> f = pool.submit(() -> {
      throw new IllegalArgumentException("bad");
    });
    System.out.println("submitted");
    try {
      f.get();
    } catch (ExecutionException e) {
      System.out.println(e.getCause());
    }
    pool.shutdown();
  }
}
// ANCHOR_END: predict-execution-exception
