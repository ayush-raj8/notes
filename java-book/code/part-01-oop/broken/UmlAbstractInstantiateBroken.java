package oop.uml;

/** Intentionally invalid: an abstract class (italic name in UML) cannot be instantiated. */
public final class UmlAbstractInstantiateBroken {
  abstract static class Shape {
    abstract double area();
  }

  public static void main(String[] args) {
    Shape s = new Shape();
  }
}
