package oop.objectc;

/** Intentionally invalid: equals(Object) must return boolean. */
public final class ObjectcEqualsReturnBroken {
  @Override
  public int equals(Object o) {
    return 1;
  }
}
