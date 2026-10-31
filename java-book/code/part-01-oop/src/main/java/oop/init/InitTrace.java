package oop.init;

// ANCHOR: init-trace
/** Prints a label and returns 0 so it can be used inside field initializers. */
public final class InitTrace {
  private InitTrace() {}

  public static int say(String label) {
    System.out.println(label);
    return 0;
  }
}
// ANCHOR_END: init-trace
