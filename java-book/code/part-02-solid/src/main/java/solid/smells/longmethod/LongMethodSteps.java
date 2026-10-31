package solid.smells.longmethod;

public final class LongMethodSteps {
  // ANCHOR: steps-1
  /** Step 1: extract only the repeated formatting. Run the tests. */
  public static final class Step1 {
    static String money(int cents) {
      return String.format("%d.%02d", cents / 100, cents % 100);
    }

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
      return "Receipt for " + customer + "\nsubtotal " + money(subtotal) + "\ndiscount " + money(discount)
          + "\nshipping " + money(shipping) + "\ntotal " + money(total);
    }
  }
  // ANCHOR_END: steps-1

  // ANCHOR: steps-2
  /** Step 2: extract the discount rule and name its numbers. Run the tests. */
  public static final class Step2 {
    public static String receipt(String customer, int[] cents, boolean member) {
      int subtotal = 0;
      for (int c : cents) {
        subtotal += c;
      }
      int discount = LongMethodDemo.After.discount(subtotal, member);
      int shipping = subtotal - discount >= 10000 ? 0 : 499;
      int total = subtotal - discount + shipping;
      return "Receipt for " + customer + "\nsubtotal " + Step1.money(subtotal) + "\ndiscount " + Step1.money(discount)
          + "\nshipping " + Step1.money(shipping) + "\ntotal " + Step1.money(total);
    }
  }
  // ANCHOR_END: steps-2

  public static void main(String[] args) {
    // ANCHOR: steps-main
    int[][] baskets = {{1999, 2999, 1500}, {1000}, {9000, 3000}, {}};
    for (int[] b : baskets) {
      for (boolean member : new boolean[] {true, false}) {
        String o = LongMethodDemo.Before.receipt("A", b, member);
        boolean s1 = o.equals(Step1.receipt("A", b, member));
        boolean s2 = o.equals(Step2.receipt("A", b, member));
        boolean s3 = o.equals(LongMethodDemo.After.receipt("A", b, member));
        System.out.println(b.length + " items member=" + member + " step1=" + s1 + " step2=" + s2 + " final=" + s3);
      }
    }
    // ANCHOR_END: steps-main
  }
}
