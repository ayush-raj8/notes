package oop.conc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// ANCHOR: executor-demo
/** Executor = separate "what to run" from "which thread runs it". Callable returns a value; Future carries it back. */
public final class ExecutorDemo {
  static long sumTo(int n) {
    long s = 0;
    for (int i = 1; i <= n; i++) s += i;
    return s;
  }

  public static void main(String[] args) throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(3);
    try {
      List<Future<Long>> futures = new ArrayList<>();
      for (int n : new int[] {10, 100, 1000}) {
        Callable<Long> task = () -> sumTo(n);
        futures.add(pool.submit(task));
      }
      for (Future<Long> f : futures) {
        System.out.println("result " + f.get()); // blocks until ready; order = submission order
      }
      List<Callable<String>> batch = List.of(() -> "a", () -> "b", () -> "c");
      for (Future<String> f : pool.invokeAll(batch)) {
        System.out.print(f.get() + " ");
      }
      System.out.println();
    } finally {
      pool.shutdown(); // stop accepting; let queued tasks finish
      System.out.println("terminated: " + pool.awaitTermination(1, TimeUnit.SECONDS));
    }
  }
}
// ANCHOR_END: executor-demo
