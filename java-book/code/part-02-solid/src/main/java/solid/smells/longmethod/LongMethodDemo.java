package solid.smells.longmethod;

public final class LongMethodDemo {
  // ANCHOR: long-before
  public static final class Before {
    /** Sums a basket, applies a member discount and a shipping rule, then formats. One method, four jobs. */
    public static String receipt(String customer, int[] cents, boolean member) {
      int subtotal = 0;
      for (int c : cents) {
        subtotal += c;
      }
      int discount = 0;
      if (member && subtotal >= 5000) {
        discount = subtotal * 10 / 100;
      } else if (member) {
        discount = subtotal * 5 / 100;
      }
      int shipping = subtotal - discount >= 10000 ? 0 : 499;
      int total = subtotal - discount + shipping;
      StringBuilder sb = new StringBuilder();
      sb.append("Receipt for ").append(customer).append('\n');
      sb.append("subtotal ").append(String.format("%d.%02d", subtotal / 100, subtotal % 100)).append('\n');
      sb.append("discount ").append(String.format("%d.%02d", discount / 100, discount % 100)).append('\n');
      sb.append("shipping ").append(String.format("%d.%02d", shipping / 100, shipping % 100)).append('\n');
      sb.append("total ").append(String.format("%d.%02d", total / 100, total % 100));
      return sb.toString();
    }
  }
  // ANCHOR_END: long-before

  // ANCHOR: long-after
  public static final class After {
    static final int MEMBER_BIG_BASKET = 5000;
    static final int MEMBER_BIG_PERCENT = 10;
    static final int MEMBER_PERCENT = 5;
    static final int FREE_SHIPPING_FROM = 10000;
    static final int SHIPPING_FEE = 499;

    public static int subtotal(int[] cents) {
      int sum = 0;
      for (int c : cents) {
        sum += c;
      }
      return sum;
    }

    public static int discount(int subtotal, boolean member) {
      if (!member) {
        return 0;
      }
      int percent = subtotal >= MEMBER_BIG_BASKET ? MEMBER_BIG_PERCENT : MEMBER_PERCENT;
      return subtotal * percent / 100;
    }

    public static int shipping(int payable) {
      return payable >= FREE_SHIPPING_FROM ? 0 : SHIPPING_FEE;
    }

    public static String money(int cents) {
      return String.format("%d.%02d", cents / 100, cents % 100);
    }

    public static String receipt(String customer, int[] cents, boolean member) {
      int subtotal = subtotal(cents);
      int discount = discount(subtotal, member);
      int shipping = shipping(subtotal - discount);
      int total = subtotal - discount + shipping;
      return String.join(
          "\n",
          "Receipt for " + customer,
          "subtotal " + money(subtotal),
          "discount " + money(discount),
          "shipping " + money(shipping),
          "total " + money(total));
    }
  }
  // ANCHOR_END: long-after

  public static void main(String[] args) {
    // ANCHOR: long-main
    int[] basket = {1999, 2999, 1500};
    String before = Before.receipt("Asha", basket, true);
    String after = After.receipt("Asha", basket, true);
    System.out.println(before);
    System.out.println("same output: " + before.equals(after));
    // ANCHOR_END: long-main
  }
}
