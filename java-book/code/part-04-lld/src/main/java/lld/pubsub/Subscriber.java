package lld.pubsub;

/** Consumer of payloads. A Subscriber<Object> can listen to any topic (PECS: consumer super). */
@FunctionalInterface
public interface Subscriber<T> {
  void onMessage(T payload);
}
