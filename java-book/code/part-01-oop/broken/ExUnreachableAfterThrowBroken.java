package oop.ex;

public class ExUnreachableAfterThrowBroken {
  static int f() {
    throw new IllegalStateException("always");
    return 1;
  }

  public static void main(String[] args) {
    f();
  }
}
