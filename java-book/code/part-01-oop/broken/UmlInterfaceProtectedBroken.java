package oop.uml;

/** Intentionally invalid: interface operations are public; "#" does not exist here. */
public final class UmlInterfaceProtectedBroken {
  interface Shape {
    protected double area();
  }
}
