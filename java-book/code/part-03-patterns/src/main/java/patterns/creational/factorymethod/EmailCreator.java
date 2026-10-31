package patterns.creational.factorymethod;

// ANCHOR: email-creator
public final class EmailCreator extends NotifierCreator {
  @Override
  protected Notifier create() {
    return new EmailNotifier();
  }
}
// ANCHOR_END: email-creator
