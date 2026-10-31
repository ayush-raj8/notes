package patterns.creational.abstractfactory;

// ANCHOR: screen
/** Client: depends only on the abstract factory and the product interfaces. */
public final class Screen {
  private final Button button;
  private final TextField field;

  public Screen(UiFactory factory) {
    this.button = factory.createButton();
    this.field = factory.createField();
  }

  public String draw() {
    return button.render() + " " + field.render();
  }
}
// ANCHOR_END: screen
