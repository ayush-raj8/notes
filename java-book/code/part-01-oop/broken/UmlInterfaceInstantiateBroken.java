package oop.uml;

/** Intentionally invalid: an <<interface>> has no constructor. */
public final class UmlInterfaceInstantiateBroken {
  interface Stack {
    void push(int x);
  }

  public static void main(String[] args) {
    Stack s = new Stack();
  }
}
