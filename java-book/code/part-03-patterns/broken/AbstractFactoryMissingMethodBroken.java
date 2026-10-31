package patterns.creational.abstractfactory;

/** Intentionally invalid: concrete factory forgets one product. */
public final class AbstractFactoryMissingMethodBroken implements UiFactory {
  @Override
  public Button createButton() {
    return () -> "[x]";
  }
}
