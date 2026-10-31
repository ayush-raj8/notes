package solid.smells.exam;

// ANCHOR: predict-swap-args
import solid.smells.params.LongParamsDemo.Before;

public final class PredictSwapArgs {
  public static void main(String[] args) {
    System.out.println(Before.book("Asha", "a@x.org", "DOUBLE", 3, false, true));
    System.out.println(Before.book("Asha", "DOUBLE", "a@x.org", 3, false, true));
    System.out.println(Before.book("Asha", "a@x.org", "DOUBLE", 3, true, false));
  }
}
// ANCHOR_END: predict-swap-args
