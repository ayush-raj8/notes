package oop.poly.exam;

// ANCHOR: predict-instanceof-null
public final class PredictInstanceofNull {
  public static void main(String[] args) {
    Object o = null;
    String s = null;
    System.out.println(o instanceof Object);
    System.out.println(s instanceof String);
    Object cast = (String) o; // cast of null is allowed
    System.out.println(cast);
    if (!(o instanceof String t)) {
      System.out.println("no match");
    }
  }
}
// ANCHOR_END: predict-instanceof-null
