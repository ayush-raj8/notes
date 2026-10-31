package oop.access;

/** Intentionally invalid: override with weaker access than {@code Object.toString()}. */
public class AccessNarrowOverrideBroken {
  @Override
  String toString() {
    return "x";
  }
}
