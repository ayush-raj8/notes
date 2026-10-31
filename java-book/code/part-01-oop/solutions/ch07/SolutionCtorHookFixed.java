package oop.override.solutions;

/** Exercise 5: repair the constructor-calls-overridable trap with a factory. */
public final class SolutionCtorHookFixed {
  static class A {
    void show() {
      System.out.println("A");
    }
  }

  static class B extends A {
    private final int n;

    private B(int n) {
      this.n = n;
    }

    static B create(int n) {
      B b = new B(n);
      b.show();
      return b;
    }

    @Override
    void show() {
      System.out.println("B" + n);
    }
  }

  public static void main(String[] args) {
    B.create(7);
  }
}
