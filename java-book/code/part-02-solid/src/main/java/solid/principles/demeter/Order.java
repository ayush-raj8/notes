package solid.principles.demeter;

// ANCHOR: order
public record Order(String id, Customer customer) {
  public String shippingCity() {
    return customer.city();
  }
}
// ANCHOR_END: order
