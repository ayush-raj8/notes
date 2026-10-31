package oop.conc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

// ANCHOR: executor-lifecycle
/** Java 19+: ExecutorService is AutoCloseable. close() = shutdown() + wait. Submitting after shutdown is rejected. */
public final class ExecutorLifecycle {
  public static void main(String[] args) throws InterruptedException {
    ExecutorService single = Executors.newSingleThreadExecutor();
    single.submit(() -> System.out.println("task 1 on single thread"));
    single.submit(() -> System.out.println("task 2 after task 1"));
    single.shutdown();
    single.awaitTermination(1, TimeUnit.SECONDS); // queued tasks finish first: stable output
    try {
      single.submit(() -> System.out.println("never runs"));
    } catch (RejectedExecutionException e) {
      System.out.println("rejected after shutdown");
    }
    System.out.println("isShutdown: " + single.isShutdown());

    try (ExecutorService auto = Executors.newFixedThreadPool(2)) {
      auto.submit(() -> System.out.println("runs inside try-with-resources"));
    } // close() waits for the task
    System.out.println("after close");
  }
}
// ANCHOR_END: executor-lifecycle
