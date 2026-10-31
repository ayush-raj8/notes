package oop.immut.exam;

// ANCHOR: predict-record-array
public final class PredictRecordArray {
  record Box(int[] a, String s) {}

  public static void main(String[] args) {
    Box x = new Box(new int[] {1}, "k");
    Box y = new Box(x.a(), "k");
    Box z = new Box(new int[] {1}, "k");
    System.out.println(x.equals(y) + " " + x.equals(z));
  }
}
// ANCHOR_END: predict-record-array
