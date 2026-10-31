package oop.nested.exam;

// ANCHOR: predict-names
public final class PredictNames {
  class Inner {}

  static class Nested {}

  public static void main(String[] args) {
    class Local {}
    Object a = new Object() {};
    Object b = new Object() {};
    System.out.println(Inner.class.getName());
    System.out.println(Nested.class.getName());
    System.out.println(Local.class.getName());
    System.out.println(a.getClass().getName());
    System.out.println(b.getClass().getName());
  }
}
// ANCHOR_END: predict-names
