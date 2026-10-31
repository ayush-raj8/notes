package lld.library.exam;

import lld.library.FinePolicy;
import lld.library.PerDayFine;

public class PredictLambdaFine {
  public static void main(String[] args) {
    FinePolicy capped = days -> Math.min(days * 10, 100);
    System.out.println(capped.fineCents(3));
    System.out.println(capped.fineCents(30));
    System.out.println(FinePolicy.none().fineCents(99));
    System.out.println(new PerDayFine(5).fineCents(-4));
    System.out.println(new PerDayFine(5));
  }
}
