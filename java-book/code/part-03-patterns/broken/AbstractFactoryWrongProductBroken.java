package patterns.creational.abstractfactory;

/** Intentionally invalid: factory returns something that is not a Button. */
public final class AbstractFactoryWrongProductBroken implements UiFactory {
  @Override
  public Button createButton() {
    return "button";
  }

  @Override
  public TextField createField() {
    return () -> "(x)";
  }
}
