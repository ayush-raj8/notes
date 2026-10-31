package oop.objectc.exam;

// ANCHOR: predict-string-eq
public final class PredictStringEq {
  public static void main(String[] args) {
    String a = "hi";
    String b = "hi";
    String c = new String("hi");
    String d = "h" + "i";
    System.out.println((a == b) + " " + (a == c) + " " + (a == d) + " " + a.equals(c));
  }
}
// ANCHOR_END: predict-string-eq
