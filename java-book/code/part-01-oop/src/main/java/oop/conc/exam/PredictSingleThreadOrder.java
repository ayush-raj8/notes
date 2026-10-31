package oop.conc.exam;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// ANCHOR: predict-single-thread-order
public final class PredictSingleThreadOrder {
  public static void main(String[] args) throws InterruptedException {
    ExecutorService ex = Executors.newSingleThreadExecutor();
    for (int i = 1; i <= 3; i++) {
      int id = i;
      ex.execute(() -> System.out.println("task " + id));
    }
    ex.shutdown();
    ex.awaitTermination(1, TimeUnit.SECONDS);
    System.out.println("end");
  }
}
// ANCHOR_END: predict-single-thread-order
