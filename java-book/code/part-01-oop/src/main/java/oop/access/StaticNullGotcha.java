package oop.access;

// ANCHOR: static-null
/** A static member is chosen by the reference's declared type; the reference value is not used. */
public class StaticNullGotcha {
  public static void main(String[] args) {
    Counter c = null;
    System.out.println(c.created());
    try {
      System.out.println(c.id());
    } catch (NullPointerException e) {
      System.out.println("NullPointerException");
    }
  }
}
// ANCHOR_END: static-null
