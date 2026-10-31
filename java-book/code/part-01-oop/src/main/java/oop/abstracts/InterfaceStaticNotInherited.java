package oop.abstracts;

// ANCHOR: interface-static-not-inherited
interface Util {
  static String tag() {
    return "Util.tag";
  }

  default String hello() {
    return "hello";
  }
}

class UtilImpl implements Util {}

/** Static interface methods belong to the interface only; default methods are inherited. */
public final class InterfaceStaticNotInherited {
  public static void main(String[] args) {
    System.out.println(Util.tag());
    System.out.println(new UtilImpl().hello());
    // new UtilImpl().tag() and UtilImpl.tag() do not compile
  }
}
// ANCHOR_END: interface-static-not-inherited
