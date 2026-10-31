package oop.override.exam;

// ANCHOR: predict-overload-not-override
class OverloadParent {
  void f(Object o) {
    System.out.println("Parent.f(Object)");
  }
}

class OverloadChild extends OverloadParent {
  void f(String s) {
    System.out.println("Child.f(String)");
  }

  @Override
  void f(Object o) {
    System.out.println("Child.f(Object)");
  }
}

public final class PredictOverloadNotOverride {
  public static void main(String[] args) {
    OverloadParent p = new OverloadChild();
    p.f("s");
    OverloadChild c = new OverloadChild();
    c.f("s");
    c.f((Object) "s");
  }
}
// ANCHOR_END: predict-overload-not-override
