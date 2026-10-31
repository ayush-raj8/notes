package oop.override.exam;

// ANCHOR: predict-ctor-hook
class CtorHookA {
  CtorHookA() {
    show();
  }

  void show() {
    System.out.println("A");
  }
}

class CtorHookB extends CtorHookA {
  int n = 7;

  @Override
  void show() {
    System.out.println("B" + n);
  }
}

public final class PredictCtorHook {
  public static void main(String[] args) {
    CtorHookB b = new CtorHookB();
    b.show();
  }
}
// ANCHOR_END: predict-ctor-hook
