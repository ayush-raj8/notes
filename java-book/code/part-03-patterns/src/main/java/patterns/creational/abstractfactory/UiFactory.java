package patterns.creational.abstractfactory;

// ANCHOR: ui-factory
/** Abstract factory: one creation method per product in the family. */
public interface UiFactory {
  Button createButton();

  TextField createField();
}
// ANCHOR_END: ui-factory
