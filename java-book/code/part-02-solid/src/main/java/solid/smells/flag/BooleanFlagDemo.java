package solid.smells.flag;

public final class BooleanFlagDemo {
  // ANCHOR: flag-before
  public static final class Before {
    /** The flag chooses between two behaviours; with false, qty and unit are ignored dummies. */
    public static String describe(String item, boolean verbose, int qty, int unit) {
      if (verbose) {
        return item + " (qty " + qty + ", unit " + unit + ", total " + qty * unit + ")";
      }
      return item;
    }
  }
  // ANCHOR_END: flag-before

  // ANCHOR: flag-after
  public static final class After {
    public static String brief(String item) {
      return item;
    }

    public static String detailed(String item, int qty, int unit) {
      return item + " (qty " + qty + ", unit " + unit + ", total " + qty * unit + ")";
    }
  }
  // ANCHOR_END: flag-after

  public static void main(String[] args) {
    // ANCHOR: flag-main
    System.out.println(Before.describe("pen", false, 0, 0)); // what do the zeros mean?
    System.out.println(Before.describe("pen", true, 4, 5));
    System.out.println(After.brief("pen"));
    System.out.println(After.detailed("pen", 4, 5));
    // ANCHOR_END: flag-main
  }
}
