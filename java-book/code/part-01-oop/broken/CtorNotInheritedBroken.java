package oop.ctors;

/**
 * Intentionally invalid: constructors are not inherited. {@code Parent(int)} does not give
 * {@code Child} a {@code Child(int)}.
 */
class CtorParent {
  CtorParent(int n) {}
}

class CtorChild extends CtorParent {
  // no constructors declared — compiler inserts CtorChild() { super(); } which also fails
}

public final class CtorNotInheritedBroken {
  public static void main(String[] args) {
    CtorChild c = new CtorChild(3);
    System.out.println(c);
  }
}
