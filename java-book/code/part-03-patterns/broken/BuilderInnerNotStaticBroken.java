package patterns.creational.builder;

/** Intentionally invalid: a non-static nested Builder needs an enclosing instance. */
public final class BuilderInnerNotStaticBroken {
  static final class Product {
    final class Builder {
      Product build() {
        return new Product();
      }
    }
  }

  public static void main(String[] args) {
    Product.Builder b = new Product.Builder();
  }
}
