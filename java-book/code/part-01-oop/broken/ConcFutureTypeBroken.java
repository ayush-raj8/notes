package oop.conc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Intentionally invalid: the Callable returns int, the Future claims String. */
public final class ConcFutureTypeBroken {
  public static void main(String[] args) {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    Future<String> f = pool.submit(() -> 42);
    pool.shutdown();
  }
}
