package patterns.creational.factorymethod;

// ANCHOR: sms-notifier
public final class SmsNotifier implements Notifier {
  @Override
  public String send(String message) {
    return "SMS:" + message;
  }
}
// ANCHOR_END: sms-notifier
