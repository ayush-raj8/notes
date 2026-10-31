package oop.uml;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

// ANCHOR: multiplicity-demo
/** Multiplicity on the association end: 1, 0..1, 1..*, *, and a fixed count. */
public final class MultiplicityDemo {
  private MultiplicityDemo() {}

  record Customer(String name) {}

  record Line(String sku, int qty) {}

  record Coupon(String code) {}

  /** Order "1" --> "1" Customer ; Order "1" *-- "1..*" Line ; Order "1" --> "0..1" Coupon */
  static final class Order {
    private final Customer customer; // 1       : final, never null
    private final List<Line> lines; // 1..*     : non-empty collection
    private final Coupon coupon; // 0..1    : null-able, expose as Optional

    Order(Customer customer, List<Line> lines, Coupon coupon) {
      this.customer = Objects.requireNonNull(customer, "customer is mandatory (1)");
      if (lines.isEmpty()) throw new IllegalArgumentException("at least one line (1..*)");
      this.lines = List.copyOf(lines);
      this.coupon = coupon;
    }

    Optional<Coupon> coupon() {
      return Optional.ofNullable(coupon);
    }

    int lineCount() {
      return lines.size();
    }
  }

  /** Rectangle "1" *-- "4" Point : a fixed count becomes an array (or four fields). */
  static final class Rectangle {
    private final int[] corners = new int[4];

    int cornerCount() {
      return corners.length;
    }
  }

  public static void main(String[] args) {
    Order o = new Order(new Customer("Asha"), List.of(new Line("A1", 2)), null);
    System.out.println("lines=" + o.lineCount() + ", coupon present=" + o.coupon().isPresent());
    try {
      new Order(new Customer("x"), List.of(), null);
    } catch (IllegalArgumentException e) {
      System.out.println("1..* violated: " + e.getMessage());
    }
    try {
      new Order(null, List.of(new Line("A", 1)), null);
    } catch (NullPointerException e) {
      System.out.println("1 violated: " + e.getMessage());
    }
    System.out.println("fixed 4: " + new Rectangle().cornerCount());
  }
}
// ANCHOR_END: multiplicity-demo
