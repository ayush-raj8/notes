package oop.nested.solutions;

/** Exercise 5: inner method delegates to the outer method that has the same name. */
public final class SolutionOuterThis {
  String name() {
    return "outer";
  }

  class Inner {
    String name() {
      return "inner";
    }

    String both() {
      return name() + "+" + SolutionOuterThis.this.name();
    }
  }

  public static void main(String[] args) {
    System.out.println(new SolutionOuterThis().new Inner().both());
  }
}
