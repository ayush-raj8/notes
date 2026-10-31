package oop.abstracts.exam;

// ANCHOR: compile-interface-helpers-ok
interface CihCheck {
  boolean ok(String s);

  default boolean not(String s) {
    return !check(s);
  }

  private boolean check(String s) { // private instance helper: allowed since Java 9
    return ok(s);
  }

  private static String norm(String s) { // private static helper
    return s.trim();
  }

  static CihCheck nonBlank() {
    return s -> !norm(s).isEmpty();
  }
}

public final class CompileInterfaceHelpersOk {
  public static void main(String[] args) {
    System.out.println(CihCheck.nonBlank().ok("  "));
    System.out.println(CihCheck.nonBlank().not("  "));
  }
}
// ANCHOR_END: compile-interface-helpers-ok
