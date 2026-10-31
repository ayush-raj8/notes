package patterns.structural.facade;

// ANCHOR: order-facade
/** One call coordinates three subsystems and undoes the reservation on failure. */
public final class OrderFacade {
  private final Inventory inventory;
  private final Payment payment;
  private final Shipping shipping;

  public OrderFacade(Inventory inventory, Payment payment, Shipping shipping) {
    this.inventory = inventory;
    this.payment = payment;
    this.shipping = shipping;
  }

  public String place(String sku, int cents) {
    if (!inventory.reserve(sku)) {
      return "FAILED: out of stock " + sku;
    }
    try {
      payment.charge(cents);
    } catch (PaymentDeclinedException e) {
      inventory.release(sku);
      return "FAILED: " + e.getMessage();
    }
    return "OK " + shipping.ship(sku);
  }
}
// ANCHOR_END: order-facade
