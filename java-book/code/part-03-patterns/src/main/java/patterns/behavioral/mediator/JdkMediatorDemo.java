package patterns.behavioral.mediator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** JDK mediators: a queue between producer and consumer; an executor between callers and threads. */
public final class JdkMediatorDemo {
  // ANCHOR: jdk-demo
  public static void main(String[] args) throws Exception {
    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
    Thread consumer =
        new Thread(
            () -> {
              List<Integer> got = new ArrayList<>();
              try {
                for (int n = queue.take(); n != -1; n = queue.take()) got.add(n);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              }
              System.out.println("consumer got " + got);
            });
    consumer.start();
    for (int i = 1; i <= 5; i++) queue.put(i);
    queue.put(-1);
    consumer.join();

    ExecutorService pool = Executors.newSingleThreadExecutor();
    try {
      List<String> order = new ArrayList<>();
      for (String s : List.of("a", "b", "c")) pool.submit(() -> order.add(s));
      pool.shutdown();
      pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
      System.out.println("executor ran " + order);
    } finally {
      pool.shutdownNow();
    }
  }
  // ANCHOR_END: jdk-demo
}
