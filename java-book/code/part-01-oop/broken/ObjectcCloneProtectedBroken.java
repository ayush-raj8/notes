package oop.objectc;

/** Intentionally invalid: Object.clone() is protected; a caller outside the class cannot use it. */
public final class ObjectcCloneProtectedBroken {
  static final class Thing implements Cloneable {}

  public static void main(String[] args) {
    Thing t = new Thing();
    Object copy = t.clone();
  }
}
