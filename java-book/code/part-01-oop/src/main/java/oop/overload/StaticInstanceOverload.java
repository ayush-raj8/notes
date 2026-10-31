package oop.overload;

// ANCHOR: static-instance-overload
/** Static and instance methods can overload each other by signature (not by static-ness alone). */
public final class StaticInstanceOverload {
  public static String id(int n) {
    return "static-int";
  }

  public String id(String s) {
    return "instance-String";
  }

  public static void main(String[] args) {
    System.out.println(id(1));
    System.out.println(new StaticInstanceOverload().id("a"));
  }
}
// ANCHOR_END: static-instance-overload
