package oop.override;

// ANCHOR: override-basics
class Greeter {
  String greet(String who) {
    return "Hello " + who;
  }
}

class LoudGreeter extends Greeter {
  @Override
  String greet(String who) {
    return super.greet(who).toUpperCase() + "!";
  }
}

/** Same signature in the subclass replaces the parent body; the object's class picks the body. */
public final class OverrideBasics {
  public static void main(String[] args) {
    Greeter g = new LoudGreeter();
    System.out.println(g.greet("ann"));
    System.out.println(new Greeter().greet("ann"));
    System.out.println(g.getClass().getSimpleName());
  }
}
// ANCHOR_END: override-basics
