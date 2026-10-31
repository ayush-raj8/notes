package lld.pubsub;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Synchronous broker: publish() calls matching subscribers on the caller's thread, in
 * subscription order. Copy-on-write lists make subscribe/unsubscribe safe during publish.
 */
public class Broker {
  private static final class Entry<T> {
    final Predicate<? super T> filter;
    final Subscriber<? super T> subscriber;
    volatile boolean active = true;

    Entry(Predicate<? super T> filter, Subscriber<? super T> subscriber) {
      this.filter = filter;
      this.subscriber = subscriber;
    }
  }

  private final Map<String, Class<?>> types = new ConcurrentHashMap<>();
  private final Map<String, List<Entry<?>>> entries = new ConcurrentHashMap<>();
  private final Consumer<SubscriberFailure> onError;

  public Broker() {
    this(failure -> {});
  }

  public Broker(Consumer<SubscriberFailure> onError) {
    this.onError = Objects.requireNonNull(onError);
  }

  public <T> Subscription subscribe(Topic<T> topic, Subscriber<? super T> subscriber) {
    return subscribe(topic, payload -> true, subscriber);
  }

  public <T> Subscription subscribe(
      Topic<T> topic, Predicate<? super T> filter, Subscriber<? super T> subscriber) {
    Class<?> known = types.putIfAbsent(topic.name(), topic.type());
    if (known != null && known != topic.type()) {
      throw new IllegalArgumentException(
          "topic type mismatch: " + topic.name() + " carries " + known.getSimpleName());
    }
    Entry<T> entry = new Entry<>(filter, subscriber);
    List<Entry<?>> list =
        entries.computeIfAbsent(topic.name(), name -> new CopyOnWriteArrayList<>());
    list.add(entry);
    return new Subscription() {
      @Override
      public boolean isActive() {
        return entry.active;
      }

      @Override
      public void close() {
        entry.active = false;
        list.remove(entry);
      }
    };
  }

  /** Returns how many subscribers handled the payload without throwing. */
  public <T> int publish(Topic<T> topic, T payload) {
    Objects.requireNonNull(payload, "payload");
    Class<?> known = types.get(topic.name());
    if (known != null && known != topic.type()) {
      throw new IllegalArgumentException(
          "topic type mismatch: " + topic.name() + " carries " + known.getSimpleName());
    }
    int delivered = 0;
    for (Entry<?> raw : entries.getOrDefault(topic.name(), List.of())) {
      @SuppressWarnings("unchecked") // safe: the type registry guarantees Entry<T> for this name
      Entry<T> entry = (Entry<T>) raw;
      if (!entry.active) {
        continue;
      }
      try {
        if (entry.filter.test(payload)) {
          entry.subscriber.onMessage(payload);
          delivered++;
        }
      } catch (RuntimeException e) {
        onError.accept(new SubscriberFailure(topic.name(), payload, e));
      }
    }
    return delivered;
  }

  public int subscriberCount(Topic<?> topic) {
    return entries.getOrDefault(topic.name(), List.of()).size();
  }
}
