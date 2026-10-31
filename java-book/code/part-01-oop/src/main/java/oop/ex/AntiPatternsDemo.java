package oop.ex;

// ANCHOR: anti-patterns
public final class AntiPatternsDemo {
  static int parseSwallow(String s) {
    try {
      return Integer.parseInt(s);
    } catch (Exception e) {
      // swallowed: caller cannot tell "0" from "bad input"
      return 0;
    }
  }

  static int parseFixed(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("not a number: " + s, e);
    }
  }

  static int countWithExceptions(int[] a) {
    int i = 0;
    try {
      while (true) {
        int ignore = a[i++];
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      return i - 1;
    }
  }

  static int countWithLength(int[] a) {
    return a.length;
  }

  public static void main(String[] args) {
    System.out.println(parseSwallow("0"));
    System.out.println(parseSwallow("zero"));
    try {
      parseFixed("zero");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage() + " / cause " + e.getCause().getClass().getSimpleName());
    }
    int[] data = {5, 6, 7};
    System.out.println(countWithExceptions(data) + " " + countWithLength(data));
  }
}
// ANCHOR_END: anti-patterns
