package oop.init;

// ANCHOR: init-parent
public class InitParent {
  static int ps = InitTrace.say("Parent static field");

  static {
    InitTrace.say("Parent static block");
  }

  int pf = InitTrace.say("Parent instance field");

  {
    InitTrace.say("Parent instance block");
  }

  public InitParent() {
    InitTrace.say("Parent constructor");
  }
}
// ANCHOR_END: init-parent
