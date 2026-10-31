package oop.override;

// ANCHOR: override-ctor-trap
class TrapBase {
  TrapBase() {
    System.out.println("TrapBase ctor");
    hook();
  }

  void hook() {
    System.out.println("base hook");
  }
}

class TrapDerived extends TrapBase {
  private String label = "ready";
  private final int size = Integer.parseInt("5");

  TrapDerived() {
    System.out.println("TrapDerived ctor label=" + label);
  }

  @Override
  void hook() {
    System.out.println("derived hook label=" + label + " size=" + size);
  }
}

/** The overridden hook runs before the subclass field initializers: it sees default values. */
public final class OverrideCtorTrap {
  public static void main(String[] args) {
    new TrapDerived();
  }
}
// ANCHOR_END: override-ctor-trap
