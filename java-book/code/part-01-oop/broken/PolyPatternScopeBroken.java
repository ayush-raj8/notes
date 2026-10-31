package oop.poly;

/** A pattern variable is only in scope where the match is definitely true. */
public class PolyPatternScopeBroken {
  public static void main(String[] args) {
    Object o = "text";
    if (o instanceof String s) {
      System.out.println(s.length());
    }
    System.out.println(s);
  }
}
