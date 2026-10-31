package patterns.creational.abstractfactory;

// ANCHOR: dark-factory
public final class DarkFactory implements UiFactory {
  @Override
  public Button createButton() {
    return () -> "[DARK BUTTON]";
  }

  @Override
  public TextField createField() {
    return () -> "(DARK FIELD)";
  }

  /** Not part of UiFactory: invisible through the interface type. */
  public String contrast() {
    return "high";
  }
}
// ANCHOR_END: dark-factory
