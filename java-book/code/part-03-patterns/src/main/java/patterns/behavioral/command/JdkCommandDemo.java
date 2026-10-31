package patterns.behavioral.command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** JDK commands: {@code Runnable} and {@code Callable} are the built-in Command interfaces. */
public final class JdkCommandDemo {
  // ANCHOR: jdk-demo
  public static void main(String[] args) throws Exception {
    Deque<Runnable> queue = new ArrayDeque<>();
    StringBuilder log = new StringBuilder();
    for (String w : List.of("a", "b", "c")) {
      queue.add(() -> log.append(w));
    }
    System.out.println("queued, log=[" + log + "]");
    while (!queue.isEmpty()) {
      queue.poll().run();
    }
    System.out.println("ran, log=[" + log + "]");

    ExecutorService ex = Executors.newSingleThreadExecutor();
    try {
      Future<Integer> f = ex.submit(() -> 6 * 7);
      System.out.println("callable result " + f.get());
    } finally {
      ex.shutdown();
    }
  }
  // ANCHOR_END: jdk-demo
}
