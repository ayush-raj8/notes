package oop.contracts;

public final class ContCompareToReturnBroken implements Comparable<ContCompareToReturnBroken> {
  final int v = 0;

  @Override
  public boolean compareTo(ContCompareToReturnBroken other) {
    return v < other.v;
  }
}
