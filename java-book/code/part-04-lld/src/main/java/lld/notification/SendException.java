package lld.notification;

/** Checked: a transport failure is expected and the caller must decide (retry, fall back). */
public class SendException extends Exception {
  public SendException(String message) {
    super(message);
  }
}
