package oop.abstracts.solutions;

/** Exercise 4: a class implementing two interfaces with the same default picks both explicitly. */
public final class SolutionDiamondFix {
  interface Printer {
    default String run() {
      return "print";
    }
  }

  interface Scanner2 {
    default String run() {
      return "scan";
    }
  }

  static class Multi implements Printer, Scanner2 {
    @Override
    public String run() {
      return Printer.super.run() + "&" + Scanner2.super.run();
    }
  }

  public static void main(String[] args) {
    System.out.println(new Multi().run());
  }
}
