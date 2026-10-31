package oop.objectc;

/** Intentionally invalid: toString() is public in Object; overriding cannot reduce access. */
public final class ObjectcToStringAccessBroken {
  @Override
  String toString() {
    return "x";
  }
}
