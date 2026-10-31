package oop.overload;

// ANCHOR: overload-inherit
class Printer {
  String print(int n) {
    return "P-int";
  }
}

final class FancyPrinter extends Printer {
  String print(String s) {
    return "F-String";
  }

  String print(double d) {
    return "F-double";
  }
}

/** Overloads in subclass; compile-time type decides the applicable set. */
public final class OverloadInherit {
  public static void main(String[] args) {
    FancyPrinter f = new FancyPrinter();
    System.out.println(f.print(1));
    System.out.println(f.print("x"));
    Printer p = f;
    System.out.println(p.print(1));
  }
}
// ANCHOR_END: overload-inherit
