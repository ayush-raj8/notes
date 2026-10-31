package oop.conc.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Exercise 3: sum 1..1_000_000 by splitting the range over four tasks. */
public final class SolutionParallelSum {
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    int n = 1_000_000;
    int parts = 4;
    try (ExecutorService pool = Executors.newFixedThreadPool(parts)) {
      List<Future<Long>> fs = new ArrayList<>();
      int chunk = n / parts;
      for (int p = 0; p < parts; p++) {
        int from = p * chunk + 1;
        int to = (p + 1) * chunk;
        fs.add(pool.submit(() -> {
          long s = 0;
          for (int i = from; i <= to; i++) s += i;
          return s;
        }));
      }
      long total = 0;
      for (Future<Long> f : fs) total += f.get();
      System.out.println("sum=" + total);
    }
  }
}
