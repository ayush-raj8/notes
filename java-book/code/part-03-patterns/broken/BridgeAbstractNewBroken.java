package patterns.structural.bridge;

/** Intentionally invalid: the abstraction is abstract and cannot be instantiated. */
public final class BridgeAbstractNewBroken {
  public static void main(String[] args) {
    Shape s = new Shape(new VectorRenderer());
  }
}
