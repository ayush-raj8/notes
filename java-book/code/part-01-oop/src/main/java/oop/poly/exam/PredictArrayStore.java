package oop.poly.exam;

// ANCHOR: predict-array-store
public final class PredictArrayStore {
  public static void main(String[] args) {
    Object[] arr = new Integer[1];
    System.out.println(arr instanceof Integer[]);
    try {
      arr[0] = "s";
      System.out.println("stored");
    } catch (ArrayStoreException e) {
      System.out.println("ASE " + e.getMessage());
    }
    System.out.println(arr[0]);
  }
}
// ANCHOR_END: predict-array-store
