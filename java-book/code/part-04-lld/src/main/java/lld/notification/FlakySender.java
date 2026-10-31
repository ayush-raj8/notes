package lld.notification;

/** Decorator that fails the first N sends, then delegates. Used to show retry and fallback. */
public final class FlakySender implements NotificationSender {
  private final NotificationSender delegate;
  private int failuresLeft;
  private int calls;

  public FlakySender(NotificationSender delegate, int failures) {
    this.delegate = delegate;
    this.failuresLeft = failures;
  }

  @Override
  public Channel channel() {
    return delegate.channel();
  }

  @Override
  public void send(String address, Message message) throws SendException {
    calls++;
    if (failuresLeft > 0) {
      failuresLeft--;
      throw new SendException("transient failure on call " + calls);
    }
    delegate.send(address, message);
  }

  public int calls() {
    return calls;
  }
}
