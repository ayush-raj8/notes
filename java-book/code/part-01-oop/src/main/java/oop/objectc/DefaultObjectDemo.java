package oop.objectc;

// ANCHOR: default-object
/** What {@code Object} gives a class that overrides nothing. */
public final class DefaultObjectDemo {
  static class Plain {}

  public static void main(String[] args) {
    Plain a = new Plain();
    Plain b = new Plain();
    System.out.println("a.equals(a)=" + a.equals(a));
    System.out.println("a.equals(b)=" + a.equals(b));
    System.out.println("a.hashCode()==identityHashCode=" + (a.hashCode() == System.identityHashCode(a)));
    System.out.println("toString prefix ok=" + a.toString().startsWith("oop.objectc.DefaultObjectDemo$Plain@"));
  }
}
// ANCHOR_END: default-object
