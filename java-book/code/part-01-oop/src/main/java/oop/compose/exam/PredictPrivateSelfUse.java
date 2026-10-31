package oop.compose.exam;

// ANCHOR: predict-private-self-use
public final class PredictPrivateSelfUse {
  static class Base {
    private String name() {
      return "base";
    }

    String hello() {
      return "hi " + name();
    }
  }

  static class Sub extends Base {
    String name() {
      return "sub";
    }
  }

  public static void main(String[] args) {
    System.out.println(new Sub().hello());
  }
}
// ANCHOR_END: predict-private-self-use
