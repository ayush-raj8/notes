package lld.food;

/** Delivery fee = base + perBlock * distance; free once the subtotal reaches freeAboveCents. */
public record PricingPolicy(long baseCents, long perBlockCents, long freeAboveCents) {
  public long deliveryFee(int distance, long subtotalCents) {
    if (subtotalCents >= freeAboveCents) {
      return 0;
    }
    return baseCents + perBlockCents * distance;
  }
}
