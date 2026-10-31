package oop.compose;

/** Intentionally invalid: subclass has no way to call the parent's only constructor. */
public final class ComposeNoDefaultCtorBroken {
  static class Base {
    Base(String id) {}
  }

  static class Sub extends Base {
  }
}
