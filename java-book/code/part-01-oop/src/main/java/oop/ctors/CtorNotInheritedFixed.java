package oop.ctors;

// ANCHOR: ctor-not-inherited-fixed
class FixedParent {
  private final int n;

  FixedParent(int n) {
    this.n = n;
  }

  int n() {
    return n;
  }
}

final class FixedChild extends FixedParent {
  FixedChild(int n) {
    super(n);
  }
}

/** Fixed: subclass declares its own constructor and calls {@code super(...)}. */
public final class CtorNotInheritedFixed {
  public static void main(String[] args) {
    System.out.println(new FixedChild(3).n());
  }
}
// ANCHOR_END: ctor-not-inherited-fixed
