package patterns.creational.builder;

/** Intentionally invalid: assigning the Builder to the product type. */
public final class BuilderForgotBuildBroken {
  public static void main(String[] args) {
    Pizza p = Pizza.builder("L").cheese();
  }
}
