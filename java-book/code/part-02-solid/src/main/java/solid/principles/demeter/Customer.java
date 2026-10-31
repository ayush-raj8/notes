package solid.principles.demeter;

// ANCHOR: customer
public record Customer(String name, Address address) {
  /** Hides how the city is stored and what happens when there is no address. */
  public String city() {
    return address == null ? "unknown" : address.city();
  }
}
// ANCHOR_END: customer
