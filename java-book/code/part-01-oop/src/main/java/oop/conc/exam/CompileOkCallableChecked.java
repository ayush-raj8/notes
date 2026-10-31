package oop.conc.exam;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ANCHOR: compile-ok-callable-checked
/** Compiles. Callable.call() declares throws Exception; Runnable.run() does not. */
public final class CompileOkCallableChecked {
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    Callable<Integer> c = () -> {
      Thread.sleep(1); // checked InterruptedException is allowed here
      return 7;
    };
    ExecutorService pool = Executors.newSingleThreadExecutor();
    System.out.println(pool.submit(c).get());
    pool.shutdown();
  }
}
// ANCHOR_END: compile-ok-callable-checked
