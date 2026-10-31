package oop.access.exam;

// ANCHOR: predict-final-ref
public final class PredictFinalRef {
  public static void main(String[] args) {
    final StringBuilder sb = new StringBuilder("a");
    sb.append("b");
    final int[] arr = {1};
    arr[0] = 7;
    System.out.println(sb + " " + arr[0]);
  }
}
// ANCHOR_END: predict-final-ref
