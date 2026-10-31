package patterns.creational.prototype;

/** Intentionally invalid: the prototype base class is abstract. */
public final class PrototypeAbstractNewBroken {
  public static void main(String[] args) {
    Shape s = new Shape("x");
  }
}
