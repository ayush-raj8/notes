package oop.uml;

/** Intentionally invalid: generalization allows one superclass. */
public final class UmlMultipleExtendsBroken {
  static class A {}

  static class B {}

  static class C extends A, B {}
}
