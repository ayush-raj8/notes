package oop.nested;

/** Intentionally invalid: a local class cannot have an access modifier. */
public final class NestedLocalModifierBroken {
  public static void main(String[] args) {
    private class Local {}
  }
}
