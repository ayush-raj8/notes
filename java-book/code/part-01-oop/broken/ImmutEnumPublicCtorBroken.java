package oop.immut;

/** Intentionally invalid: enum constructors are implicitly private. */
public final class ImmutEnumPublicCtorBroken {
  enum Level {
    LOW(1);

    public Level(int w) {}
  }
}
