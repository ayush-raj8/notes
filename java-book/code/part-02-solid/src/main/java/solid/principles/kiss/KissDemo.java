package solid.principles.kiss;

import java.time.Year;

public final class KissDemo {
  // ANCHOR: kiss-before
  public static final class Before {
    /** Correct, but the reader must replay the counter to believe it. */
    public static boolean isLeap(int y) {
      int c = 0;
      if (y % 4 == 0) {
        c++;
      }
      if (y % 100 == 0) {
        c--;
      }
      if (y % 400 == 0) {
        c++;
      }
      return c == 1;
    }
  }
  // ANCHOR_END: kiss-before

  // ANCHOR: kiss-after
  public static final class After {
    public static boolean isLeap(int y) {
      return Year.isLeap(y);
    }

    /** If the JDK method were not available, the rule as written in the calendar: */
    public static boolean isLeapByRule(int y) {
      return (y % 4 == 0 && y % 100 != 0) || y % 400 == 0;
    }
  }
  // ANCHOR_END: kiss-after

  public static void main(String[] args) {
    // ANCHOR: kiss-main
    for (int y : new int[] {1900, 2000, 2024, 2100}) {
      System.out.println(y + " " + Before.isLeap(y) + " " + After.isLeap(y) + " " + After.isLeapByRule(y));
    }
    // ANCHOR_END: kiss-main
  }
}
