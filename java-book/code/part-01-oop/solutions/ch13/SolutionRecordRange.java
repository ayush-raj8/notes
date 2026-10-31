package oop.immut.solutions;

/** Exercise 2: record with a compact constructor that rejects bad input. */
public final class SolutionRecordRange {
  record Percent(int value) {
    Percent {
      if (value < 0 || value > 100) {
        throw new IllegalArgumentException("0..100 expected: " + value);
      }
    }
  }

  public static void main(String[] args) {
    System.out.println(new Percent(40));
    try {
      new Percent(140);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
