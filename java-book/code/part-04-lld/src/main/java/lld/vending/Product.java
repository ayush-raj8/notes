package lld.vending;

// ANCHOR: product
public record Product(String code, String name, int price) {
  public Product {
    if (price <= 0) {
      throw new IllegalArgumentException("price must be positive");
    }
  }
}
// ANCHOR_END: product
