package lld.pubsub;

/** Handle returned by subscribe. AutoCloseable so try-with-resources unsubscribes. */
public interface Subscription extends AutoCloseable {
  boolean isActive();

  /** Idempotent; narrows AutoCloseable.close() so callers need no catch. */
  @Override
  void close();
}
