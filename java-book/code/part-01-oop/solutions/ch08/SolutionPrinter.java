package oop.compare.solutions;

/** Exercise 4: one overload and one override in the same pair; predict then run. */
public final class SolutionPrinter {
  static class Printer {
    String print(Object o) {
      return "Printer.Object";
    }
  }

  static class Fancy extends Printer {
    @Override
    String print(Object o) {
      return "Fancy.Object";
    }

    String print(String s) {
      return "Fancy.String";
    }
  }

  public static void main(String[] args) {
    Printer p = new Fancy();
    Fancy f = new Fancy();
    System.out.println(p.print("a"));
    System.out.println(f.print("a"));
    System.out.println(f.print(1));
  }
}
