package oop.objectc.exam;

// ANCHOR: predict-null-string
public final class PredictNullString {
  public static void main(String[] args) {
    String s = null;
    Object o = null;
    System.out.println(s + "!");
    System.out.println(o);
    System.out.println(String.valueOf(o).length());
    try {
      System.out.println(o.toString());
    } catch (NullPointerException e) {
      System.out.println("NPE");
    }
  }
}
// ANCHOR_END: predict-null-string
