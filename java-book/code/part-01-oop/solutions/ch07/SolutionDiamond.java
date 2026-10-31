package oop.override.solutions;

/** Exercise 3: resolve two conflicting defaults. */
public final class SolutionDiamond {
  interface Printer {
    default String out() {
      return "print";
    }
  }

  interface Scanner {
    default String out() {
      return "scan";
    }
  }

  static class Multi implements Printer, Scanner {
    @Override
    public String out() {
      return Printer.super.out() + "&" + Scanner.super.out();
    }
  }

  public static void main(String[] args) {
    System.out.println(new Multi().out());
  }
}
