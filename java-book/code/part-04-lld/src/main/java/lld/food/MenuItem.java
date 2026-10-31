package lld.food;

public record MenuItem(String id, String name, long priceCents) {
  public MenuItem {
    if (priceCents <= 0) {
      throw new IllegalArgumentException("price must be positive: " + id);
    }
  }
}
