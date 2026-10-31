package oop.ex;

// ANCHOR: errors
public final class ErrorsDemo {
  static int depth = 0;

  static void recurse() {
    depth++;
    recurse();
  }

  public static void main(String[] args) {
    try {
      recurse();
    } catch (StackOverflowError e) {
      System.out.println("StackOverflowError, depth > 1000: " + (depth > 1000));
      System.out.println("is Error: " + (e instanceof Error));
      System.out.println("is Exception: " + Exception.class.isInstance(e));
    }

    try {
      long[] huge = new long[Integer.MAX_VALUE];
      System.out.println(huge.length);
    } catch (OutOfMemoryError e) {
      System.out.println("OutOfMemoryError: " + e.getMessage());
    }

    try {
      int[] neg = new int[-1];
      System.out.println(neg.length);
    } catch (NegativeArraySizeException e) {
      System.out.println("NegativeArraySizeException: " + e.getMessage());
    }
  }
}
// ANCHOR_END: errors
