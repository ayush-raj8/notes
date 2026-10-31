package oop.init;

// ANCHOR: parent-child-demo
public final class ParentChildDemo {
  public static void main(String[] args) {
    InitTrace.say("main start");
    new InitChild();
    InitTrace.say("-- second object --");
    new InitChild();
  }
}
// ANCHOR_END: parent-child-demo
