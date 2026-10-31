package patterns.structural.facade;

// ANCHOR: facade-shipping
public class Shipping {
  public String ship(String sku) {
    return "TRK-" + sku.toUpperCase();
  }
}
// ANCHOR_END: facade-shipping
