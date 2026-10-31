package solid.ocp.before;

// ANCHOR: discount-before
public final class DiscountBefore {
  /** Discount amount in cents. Every new customer type means editing this method. */
  public static long discount(String type, long cents) {
    if (type.equals("REGULAR")) {
      return 0;
    } else if (type.equals("VIP")) {
      return cents * 10 / 100;
    } else if (type.equals("STUDENT")) {
      return cents * 5 / 100;
    }
    return 0; // unknown types silently get nothing
  }

  /** Second chain over the same types, edited for EMPLOYEE; discount() was not. */
  public static String label(String type) {
    if (type.equals("VIP")) {
      return "gold";
    } else if (type.equals("STUDENT")) {
      return "campus";
    } else if (type.equals("EMPLOYEE")) {
      return "staff";
    }
    return "standard";
  }
}
// ANCHOR_END: discount-before
