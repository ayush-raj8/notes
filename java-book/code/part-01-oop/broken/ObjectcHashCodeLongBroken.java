package oop.objectc;

/** Intentionally invalid: hashCode() must return int. */
public final class ObjectcHashCodeLongBroken {
  @Override
  public long hashCode() {
    return 1L;
  }
}
