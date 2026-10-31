package oop.init;

// ANCHOR: ordered-class
class Ordered {
  static int s1 = InitTrace.say("static field s1");

  static {
    InitTrace.say("static block");
  }

  static int s2 = InitTrace.say("static field s2");

  int f1 = InitTrace.say("instance field f1");

  {
    InitTrace.say("instance block");
  }

  int f2 = InitTrace.say("instance field f2");

  Ordered() {
    InitTrace.say("constructor");
  }
}
// ANCHOR_END: ordered-class

// ANCHOR: instance-order-demo
public final class InstanceOrderDemo {
  public static void main(String[] args) {
    new Ordered();
    InitTrace.say("-- second object --");
    new Ordered();
  }
}
// ANCHOR_END: instance-order-demo
