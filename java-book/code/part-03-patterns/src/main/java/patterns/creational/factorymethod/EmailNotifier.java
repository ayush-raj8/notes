package patterns.creational.factorymethod;

// ANCHOR: email-notifier
public final class EmailNotifier implements Notifier {
  @Override
  public String send(String message) {
    return "EMAIL:" + message;
  }
}
// ANCHOR_END: email-notifier
