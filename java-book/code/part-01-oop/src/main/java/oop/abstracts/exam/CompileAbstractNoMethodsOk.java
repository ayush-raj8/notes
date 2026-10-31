package oop.abstracts.exam;

// ANCHOR: compile-abstract-no-methods-ok
abstract class CanBase {}

abstract class CanDerived extends CanBase {
  abstract void run();
}

class CanConcrete extends CanDerived {
  @Override
  void run() {
    System.out.println("run");
  }
}

public final class CompileAbstractNoMethodsOk {
  public static void main(String[] args) {
    CanBase b = new CanConcrete();
    ((CanConcrete) b).run();
    System.out.println(b instanceof CanDerived);
  }
}
// ANCHOR_END: compile-abstract-no-methods-ok
