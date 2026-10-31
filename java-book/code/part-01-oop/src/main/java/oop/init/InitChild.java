package oop.init;

// ANCHOR: init-child
public class InitChild extends InitParent {
  static int cs = InitTrace.say("Child static field");

  static {
    InitTrace.say("Child static block");
  }

  int cf = InitTrace.say("Child instance field");

  {
    InitTrace.say("Child instance block");
  }

  public InitChild() {
    InitTrace.say("Child constructor");
  }
}
// ANCHOR_END: init-child
