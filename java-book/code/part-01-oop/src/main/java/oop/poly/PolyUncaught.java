package oop.poly;

// ANCHOR: poly-uncaught
public final class PolyUncaught {
  public static void main(String[] args) {
    System.out.println("before");
    Object o = Integer.valueOf(1);
    String s = (String) o;
    System.out.println("after " + s);
  }
}
// ANCHOR_END: poly-uncaught
