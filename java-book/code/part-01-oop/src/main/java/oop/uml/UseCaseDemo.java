package oop.uml;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: usecase-demo
/** Use-case diagram -> code: an actor becomes an interface, a use case becomes a method. */
public final class UseCaseDemo {
  private UseCaseDemo() {}

  interface CustomerActions { // actor: Customer
    String placeOrder(String sku, String coupon);
  }

  interface AdminActions { // actor: Admin
    void addProduct(String sku, int price);
  }

  static final class Shop implements CustomerActions, AdminActions {
    final List<String> trace = new ArrayList<>();
    private int price = 100;

    @Override
    public String placeOrder(String sku, String coupon) {
      trace.add("placeOrder(" + sku + ")");
      if (coupon != null) { // <<extend>>: optional extra behaviour, guarded by a condition
        applyCoupon(coupon);
      }
      pay(); // <<include>>: always part of the base use case
      return "ordered " + sku + " for " + price;
    }

    private void applyCoupon(String coupon) {
      trace.add("applyCoupon(" + coupon + ")");
      price -= 10;
    }

    private void pay() {
      trace.add("pay()");
    }

    @Override
    public void addProduct(String sku, int price) {
      trace.add("addProduct(" + sku + ")");
      this.price = price;
    }
  }

  public static void main(String[] args) {
    Shop shop = new Shop();
    AdminActions admin = shop;
    CustomerActions customer = shop;
    admin.addProduct("A1", 100);
    System.out.println(customer.placeOrder("A1", null));
    System.out.println(customer.placeOrder("A1", "SAVE10"));
    System.out.println(shop.trace);
  }
}
// ANCHOR_END: usecase-demo
