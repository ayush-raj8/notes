package oop.objectc.solutions;

/** Exercise 2: readable toString. */
public final class SolutionToString {
  static final class Item {
    private final String sku;
    private final int qty;

    Item(String sku, int qty) {
      this.sku = sku;
      this.qty = qty;
    }

    @Override
    public String toString() {
      return "Item[sku=" + sku + ", qty=" + qty + "]";
    }
  }

  public static void main(String[] args) {
    System.out.println(new Item("A1", 3));
  }
}
