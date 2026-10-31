package patterns.creational.abstractfactory;

// ANCHOR: light-factory
public final class LightFactory implements UiFactory {
  @Override
  public Button createButton() {
    return () -> "[light button]";
  }

  @Override
  public TextField createField() {
    return () -> "(light field)";
  }
}
// ANCHOR_END: light-factory
