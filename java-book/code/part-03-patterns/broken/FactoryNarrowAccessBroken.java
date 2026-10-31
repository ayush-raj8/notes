package patterns.creational.factorymethod;

/** Intentionally invalid: override weakens protected to package-private. */
public final class FactoryNarrowAccessBroken extends NotifierCreator {
  @Override
  Notifier create() {
    return new SmsNotifier();
  }
}
