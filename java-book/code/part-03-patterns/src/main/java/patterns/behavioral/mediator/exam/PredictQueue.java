package patterns.behavioral.mediator.exam;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

// ANCHOR: predict-queue
public final class PredictQueue {
  public static void main(String[] args) throws InterruptedException {
    BlockingQueue<String> q = new LinkedBlockingQueue<>();
    System.out.println(q.poll());
    System.out.println(q.poll(10, TimeUnit.MILLISECONDS));
    System.out.println(q.offer("a") + " " + q.offer("b"));
    System.out.println(q.take() + q.take());
    System.out.println(q.isEmpty());
    try {
      q.put(null);
    } catch (NullPointerException e) {
      System.out.println("NullPointerException");
    }
  }
}
// ANCHOR_END: predict-queue
