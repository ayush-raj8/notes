package oop.uml;

/** Intentionally invalid: a realizing class must implement every abstract operation. */
public final class UmlRealizeMissingBroken {
  interface Payable {
    int pay();

    String id();
  }

  static class Employee implements Payable {
    public int pay() {
      return 1;
    }
  }
}
