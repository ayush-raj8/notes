package patterns.structural.flyweight;

/** Intentionally invalid: the factory key is (name, color). */
public final class FlyWrongArgsBroken {
  public static void main(String[] args) {
    TreeType t = TreeFactory.get("oak");
  }
}
