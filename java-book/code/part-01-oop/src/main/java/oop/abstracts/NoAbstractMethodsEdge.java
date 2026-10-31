package oop.abstracts;

// ANCHOR: no-abstract-methods-edge
abstract class OnlyBase {
  String hello() {
    return "concrete method in abstract class";
  }
}

interface Marker {}

/** abstract does not require abstract methods; an interface may be empty (marker). */
public final class NoAbstractMethodsEdge {
  public static void main(String[] args) {
    OnlyBase b = new OnlyBase() {};
    System.out.println(b.hello());
    Marker m = new Marker() {};
    System.out.println(m instanceof Marker);
    System.out.println(OnlyBase.class.getModifiers() == java.lang.reflect.Modifier.ABSTRACT);
  }
}
// ANCHOR_END: no-abstract-methods-edge
