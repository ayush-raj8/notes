package oop.overload;

// ANCHOR: overload-main
/** Overloading {@code main} is legal; JVM still starts {@code main(String[])}. */
public final class OverloadMain {
  public static void main(String[] args) {
    System.out.println("String[]");
    main(3);
  }

  public static void main(int n) {
    System.out.println("int:" + n);
  }
}
// ANCHOR_END: overload-main
