package oop.compose;

/** Intentionally invalid: a subclass cannot touch the parent's private state. */
public final class ComposePrivateFieldBroken {
  static class Base {
    private int count;
  }

  static class Sub extends Base {
    void bump() {
      count++;
    }
  }
}
