package solid.smells.exam;

// ANCHOR: predict-flag-call
import solid.smells.flag.BooleanFlagDemo.Before;

public final class PredictFlagCall {
  public static void main(String[] args) {
    System.out.println(Before.describe("ink", false, 3, 7));
    System.out.println(Before.describe("ink", true, 3, 7));
    System.out.println(Before.describe("ink", true, 0, 0));
    System.out.println(Before.describe("ink", true, 3, 7).length());
  }
}
// ANCHOR_END: predict-flag-call
