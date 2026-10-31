package oop.nested;

/** Intentionally invalid: outer.new is for inner classes; a static nested class uses plain new. */
public final class NestedOuterNewWrongBroken {
  static class Sn {}

  public static void main(String[] args) {
    NestedOuterNewWrongBroken o = new NestedOuterNewWrongBroken();
    Sn s = o.new Sn();
    System.out.println(s);
  }
}
