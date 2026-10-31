package patterns.behavioral.state;

import java.util.concurrent.FutureTask;

/** JDK states: {@code Thread.State} and {@code Future.State} (Java 19+). */
public final class JdkStateDemo {
  // ANCHOR: jdk-demo
  public static void main(String[] args) throws Exception {
    Thread t = new Thread(() -> {});
    System.out.println(t.getState());
    t.start();
    t.join();
    System.out.println(t.getState());

    FutureTask<Integer> done = new FutureTask<>(() -> 7);
    System.out.println(done.state());
    done.run();
    System.out.println(done.state() + " " + done.resultNow());

    FutureTask<Integer> cancelled = new FutureTask<>(() -> 8);
    cancelled.cancel(false);
    System.out.println(cancelled.state());

    FutureTask<Integer> failed =
        new FutureTask<>(
            () -> {
              throw new IllegalStateException("bad");
            });
    failed.run();
    System.out.println(failed.state() + " " + failed.exceptionNow().getMessage());
  }
  // ANCHOR_END: jdk-demo
}
