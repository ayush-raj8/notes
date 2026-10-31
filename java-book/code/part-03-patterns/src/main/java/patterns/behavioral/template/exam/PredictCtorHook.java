package patterns.behavioral.template.exam;

// ANCHOR: predict-ctor-hook
public final class PredictCtorHook {
  abstract static class Base {
    Base() {
      System.out.println(describe());
    }

    abstract String describe();
  }

  static final class Child extends Base {
    final String constant = "const";
    final String computed = String.valueOf("calc");
    String plain = "plain";

    @Override
    String describe() {
      return constant + "," + computed + "," + plain;
    }
  }

  public static void main(String[] args) {
    Child c = new Child();
    System.out.println(c.describe());
  }
}
// ANCHOR_END: predict-ctor-hook
