package patterns.creational.builder;

/** Intentionally invalid: the product constructor is private. */
public final class BuilderPrivateCtorBroken {
  public static void main(String[] args) {
    Pizza p = new Pizza(null);
  }
}
