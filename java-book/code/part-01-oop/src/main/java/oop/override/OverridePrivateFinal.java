package oop.override;

// ANCHOR: override-private-final
class SealedParent {
  public String run() {
    return secret() + "|" + locked();
  }

  private String secret() {
    return "parent-private";
  }

  final String locked() {
    return "parent-final";
  }
}

class SealedChild extends SealedParent {
  /** Not an override: private methods are not inherited, so this is a brand-new method. */
  String secret() {
    return "child-secret";
  }
}

/** private and final methods cannot be overridden. */
public final class OverridePrivateFinal {
  public static void main(String[] args) {
    SealedChild c = new SealedChild();
    System.out.println(c.run());
    System.out.println(c.secret());
    SealedParent p = c;
    System.out.println(p.locked());
  }
}
// ANCHOR_END: override-private-final
