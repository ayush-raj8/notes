package oop.compose.exam;

// ANCHOR: predict-self-use
public final class PredictSelfUse {
  static class Base {
    String name() {
      return "base";
    }

    String hello() {
      return "hi " + name();
    }
  }

  static class Sub extends Base {
    @Override
    String name() {
      return "sub";
    }
  }

  public static void main(String[] args) {
    Base b = new Sub();
    System.out.println(b.hello());
  }
}
// ANCHOR_END: predict-self-use
