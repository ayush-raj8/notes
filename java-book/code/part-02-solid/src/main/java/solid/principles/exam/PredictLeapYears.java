package solid.principles.exam;

// ANCHOR: predict-leap-years
import solid.principles.kiss.KissDemo;

public final class PredictLeapYears {
  public static void main(String[] args) {
    for (int y : new int[] {1900, 2000, 2023, 2024}) {
      System.out.println(y + " " + KissDemo.Before.isLeap(y) + " " + KissDemo.After.isLeapByRule(y));
    }
  }
}
// ANCHOR_END: predict-leap-years
