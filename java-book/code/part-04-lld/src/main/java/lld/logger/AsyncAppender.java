package lld.logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

// ANCHOR: async-appender
/**
 * Decorator: callers enqueue and return; one worker thread feeds the delegate. A bounded queue
 * applies back-pressure (producers block when it is full). {@code close} drains, then stops.
 */
public final class AsyncAppender implements Appender, AutoCloseable {
  private static final LogEvent STOP = new LogEvent(-1, Level.ERROR, "", "");

  private final BlockingQueue<LogEvent> queue;
  private final Appender delegate;
  private final Thread worker;

  public AsyncAppender(Appender delegate, int capacity) {
    this.delegate = delegate;
    this.queue = new ArrayBlockingQueue<>(capacity);
    this.worker = new Thread(this::drain, "async-appender");
    this.worker.setDaemon(true);
    this.worker.start();
  }

  @Override
  public void append(LogEvent event) {
    try {
      queue.put(event);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void drain() {
    try {
      while (true) {
        LogEvent e = queue.take();
        if (e == STOP) {
          return;
        }
        delegate.append(e);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void close() {
    try {
      queue.put(STOP);
      worker.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
// ANCHOR_END: async-appender
