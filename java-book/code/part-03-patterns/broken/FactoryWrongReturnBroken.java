package patterns.creational.factorymethod;

/** Intentionally invalid: factory method returns a type unrelated to Notifier. */
public final class FactoryWrongReturnBroken extends NotifierCreator {
  @Override
  protected String create() {
    return "email";
  }
}
