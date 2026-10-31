package oop.immut;

/** Intentionally invalid: an enum cannot be extended. */
public final class ImmutEnumExtendBroken {
  enum Level { LOW, HIGH }

  static class Custom extends Level {}
}
