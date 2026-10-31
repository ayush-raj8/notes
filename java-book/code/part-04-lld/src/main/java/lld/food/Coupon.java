package lld.food;

/** Percentage off the subtotal, capped. Integer maths: floor(subtotal * percent / 100). */
public record Coupon(String code, int percentOff, long maxDiscountCents) {
  public Coupon {
    if (percentOff < 1 || percentOff > 100) {
      throw new IllegalArgumentException("percentOff must be 1..100");
    }
  }

  public long discount(long subtotalCents) {
    return Math.min(subtotalCents * percentOff / 100, maxDiscountCents);
  }
}
