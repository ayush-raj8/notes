package oop.compose;

/** Intentionally invalid: a class extends exactly one class. */
public final class ComposeMultiExtendsBroken {
  static class A {}

  static class B {}

  static class C extends A, B {}
}
