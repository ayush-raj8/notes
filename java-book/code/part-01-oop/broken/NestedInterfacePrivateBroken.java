package oop.nested;

/** Intentionally invalid: interface members are implicitly public, so a nested type cannot be private. */
public interface NestedInterfacePrivateBroken {
  private class Hidden {}
}
