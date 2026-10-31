package oop.nested;

// ANCHOR: names-demo
/** Each nested class compiles to its own .class file: Outer$Inner, Outer$1, Outer$1Local. */
public final class NamesDemo {
  static class Sn {}

  class In {}

  public static void main(String[] args) {
    class Local {}
    Object anon = new Object() {};
    System.out.println(Sn.class.getName());
    System.out.println(In.class.getName());
    System.out.println(Local.class.getName());
    System.out.println(anon.getClass().getName());
    System.out.println(Sn.class.getSimpleName());
    System.out.println("[" + anon.getClass().getSimpleName() + "]");
  }
}
// ANCHOR_END: names-demo
