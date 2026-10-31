package lld.pubsub;

/** A subscriber threw. The broker reports it and keeps delivering to the others. */
public record SubscriberFailure(String topic, Object payload, Throwable error) {}
