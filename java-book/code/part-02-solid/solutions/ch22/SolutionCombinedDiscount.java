package solid.ocp.solutions;

import solid.ocp.rules.Discount;
import solid.ocp.rules.PercentDiscount;

/** Exercise 2: a rule built from two rules; the best one wins. */
public final class SolutionCombinedDiscount {
  record Best(Discount a, Discount b) implements Discount {
    @Override
    public long amount(long cents) {
      return Math.max(a.amount(cents), b.amount(cents));
    }
  }

  public static void main(String[] args) {
    Discount best = new Best(new PercentDiscount(10), cents -> 500);
    System.out.println(best.amount(2_000));
    System.out.println(best.amount(10_000));
  }
}
