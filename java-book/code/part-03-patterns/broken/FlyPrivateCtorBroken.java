package patterns.structural.flyweight;

/** Intentionally invalid: callers must go through the factory, so the constructor is hidden. */
public final class FlyPrivateCtorBroken {
  public static void main(String[] args) {
    TreeFactory f = new TreeFactory();
  }
}
