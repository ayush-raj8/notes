package oop.init;

// ANCHOR: override-in-ctor
class BaseGreeter {
  BaseGreeter() {
    System.out.println("Base ctor sees: " + label());
  }

  String label() {
    return "base";
  }
}

class LoudGreeter extends BaseGreeter {
  private String text = "loud";

  LoudGreeter() {
    System.out.println("Loud ctor sees: " + label());
  }

  @Override
  String label() {
    return text;
  }
}

public final class OverrideInCtorGotcha {
  public static void main(String[] args) {
    new LoudGreeter();
  }
}
// ANCHOR_END: override-in-ctor
