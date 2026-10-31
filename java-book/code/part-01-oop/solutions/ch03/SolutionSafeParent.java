package oop.init.solutions;

class SafeBase {
  private final String text;

  SafeBase(String text) {
    this.text = text;
    System.out.println("Base ctor sees: " + this.text);
  }
}

class SafeLoud extends SafeBase {
  SafeLoud() {
    super("loud");
    System.out.println("Loud ctor done");
  }
}

/** Exercise 2: fix the overridable-call-in-constructor trap by passing data up via super(...). */
public final class SolutionSafeParent {
  public static void main(String[] args) {
    new SafeLoud();
  }
}
