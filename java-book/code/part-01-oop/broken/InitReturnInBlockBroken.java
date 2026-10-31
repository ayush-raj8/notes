package oop.init;

/** Intentionally invalid: return inside an instance initializer. */
public final class InitReturnInBlockBroken {
  {
    return;
  }
}
