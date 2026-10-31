package oop.contracts;

public final class ContComparableWrongTypeBroken implements Comparable<ContComparableWrongTypeBroken> {
  final int v = 0;

  public int compareTo(Object other) {
    return 0;
  }
}
