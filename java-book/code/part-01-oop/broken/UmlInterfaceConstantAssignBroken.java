package oop.uml;

/** Intentionally invalid: interface fields are public static final. */
public final class UmlInterfaceConstantAssignBroken {
  interface Limits {
    int MAX = 10;
  }

  public static void main(String[] args) {
    Limits.MAX = 20;
  }
}
