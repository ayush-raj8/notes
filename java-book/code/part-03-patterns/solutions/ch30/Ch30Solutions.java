package patterns.creational.abstractfactory.solutions;

import patterns.creational.abstractfactory.Button;
import patterns.creational.abstractfactory.Screen;
import patterns.creational.abstractfactory.TextField;
import patterns.creational.abstractfactory.UiFactory;

public final class Ch30Solutions {
  // ANCHOR: ch30-ex1
  /** Ex1: add a third family without editing existing factories. */
  static class RetroFactory implements UiFactory {
    @Override
    public Button createButton() {
      return () -> "<retro button>";
    }

    @Override
    public TextField createField() {
      return () -> "<retro field>";
    }
  }
  // ANCHOR_END: ch30-ex1

  // ANCHOR: ch30-ex2
  /** Ex2: a new product (Checkbox) forces a change in the interface and every factory. */
  interface Checkbox {
    String render();
  }

  interface WideUiFactory extends UiFactory {
    Checkbox createCheckbox();
  }

  static final class WideRetro extends RetroFactory implements WideUiFactory {
    @Override
    public Checkbox createCheckbox() {
      return () -> "<retro box>";
    }
  }
  // ANCHOR_END: ch30-ex2

  // ANCHOR: ch30-ex3
  /** Ex3: factory as a lambda-friendly family builder via a record. */
  record SimpleFactory(String button, String field) implements UiFactory {
    @Override
    public Button createButton() {
      return () -> button;
    }

    @Override
    public TextField createField() {
      return () -> field;
    }
  }
  // ANCHOR_END: ch30-ex3

  // ANCHOR: ch30-ex4
  /** Ex4: factory that counts how many products it created. */
  static final class CountingFactory implements UiFactory {
    private final UiFactory inner;
    int created;

    CountingFactory(UiFactory inner) {
      this.inner = inner;
    }

    @Override
    public Button createButton() {
      created++;
      return inner.createButton();
    }

    @Override
    public TextField createField() {
      created++;
      return inner.createField();
    }
  }
  // ANCHOR_END: ch30-ex4

  // ANCHOR: ch30-ex5
  /** Ex5: client accepts one factory so a mixed family is impossible. */
  static String screenFor(UiFactory f) {
    return new Screen(f).draw();
  }
  // ANCHOR_END: ch30-ex5

  public static void main(String[] args) {
    System.out.println("ex1 " + screenFor(new RetroFactory()));
    System.out.println("ex2 " + new WideRetro().createCheckbox().render());
    System.out.println("ex3 " + screenFor(new SimpleFactory("B", "F")));
    CountingFactory cf = new CountingFactory(new RetroFactory());
    screenFor(cf);
    System.out.println("ex4 created=" + cf.created);
    System.out.println("ex5 " + screenFor(new RetroFactory()));
  }
}
