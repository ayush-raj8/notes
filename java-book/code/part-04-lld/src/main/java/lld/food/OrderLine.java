package lld.food;

public record OrderLine(MenuItem item, int quantity) {
  public OrderLine {
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity must be positive: " + item.id());
    }
  }

  public long totalCents() {
    return item.priceCents() * quantity;
  }
}
