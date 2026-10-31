package oop.overload;

// ANCHOR: varargs-edge
/** Edge: fixed-arity beats varargs when both apply. */
public final class VarargsEdge {
  public static String f(int a, int b) {
    return "fixed";
  }

  public static String f(int... a) {
    return "varargs len=" + a.length;
  }

  public static void main(String[] args) {
    System.out.println(f(1, 2));
    System.out.println(f(1));
    System.out.println(f());
  }
}
// ANCHOR_END: varargs-edge
