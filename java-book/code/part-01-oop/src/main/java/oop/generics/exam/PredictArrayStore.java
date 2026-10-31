package oop.generics.exam;

// ANCHOR: predict-array-store
public final class PredictArrayStore {
  public static void main(String[] args) {
    Number[] nums = new Integer[2];
    nums[0] = 1;
    System.out.println("first");
    nums[1] = 2.5;
    System.out.println("second");
  }
}
// ANCHOR_END: predict-array-store
