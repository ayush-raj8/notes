package patterns.creational.factorymethod;

// ANCHOR: sms-creator
public final class SmsCreator extends NotifierCreator {
  @Override
  protected Notifier create() {
    return new SmsNotifier();
  }
}
// ANCHOR_END: sms-creator
