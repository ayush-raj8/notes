package oop.compare.solutions;

/** Exercise 2: static is chosen by the reference type; instance by the object. */
public final class SolutionStaticHide {
  static class Parent {
    static String name() {
      return "Parent";
    }

    String who() {
      return "Parent";
    }
  }

  static class Child extends Parent {
    static String name() {
      return "Child";
    }

    @Override
    String who() {
      return "Child";
    }
  }

  public static void main(String[] args) {
    Parent p = new Child();
    System.out.println(p.name() + " " + p.who());
    System.out.println(Child.name() + " " + ((Child) p).who());
  }
}
