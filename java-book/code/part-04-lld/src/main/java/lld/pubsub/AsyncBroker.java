package lld.pubsub;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Decorates Broker: every subscriber gets its own single-thread queue, so one subscriber sees
 * messages in publish order and a slow subscriber never blocks the publisher or the others.
 */
public final class AsyncBroker implements AutoCloseable {
  private final Broker inner;
  private final Consumer<SubscriberFailure> onError;
  private final List<ExecutorService> workers = new CopyOnWriteArrayList<>();

  public AsyncBroker(Consumer<SubscriberFailure> onError) {
    this.onError = onError;
    this.inner = new Broker(onError);
  }

  public <T> Subscription subscribe(Topic<T> topic, Subscriber<? super T> subscriber) {
    ExecutorService worker =
        Executors.newSingleThreadExecutor(
            r -> {
              Thread t = new Thread(r, "subscriber-" + topic.name());
              t.setDaemon(true);
              return t;
            });
    workers.add(worker);
    Subscription handle =
        inner.subscribe(
            topic,
            payload ->
                worker.execute(
                    () -> {
                      try {
                        subscriber.onMessage(payload);
                      } catch (RuntimeException e) {
                        onError.accept(new SubscriberFailure(topic.name(), payload, e));
                      }
                    }));
    return new Subscription() {
      @Override
      public boolean isActive() {
        return handle.isActive();
      }

      @Override
      public void close() {
        handle.close();
        worker.shutdown(); // queued messages still drain
      }
    };
  }

  /** Returns how many subscriber queues accepted the payload. */
  public <T> int publish(Topic<T> topic, T payload) {
    return inner.publish(topic, payload);
  }

  /** Stops accepting work and waits for queued messages to be handled. */
  @Override
  public void close() {
    for (ExecutorService w : workers) {
      w.shutdown();
    }
    for (ExecutorService w : workers) {
      try {
        if (!w.awaitTermination(5, TimeUnit.SECONDS)) {
          w.shutdownNow();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        w.shutdownNow();
      }
    }
  }
}
