package oop.override;

/** Not allowed: compareTo(Object) clashes with the erasure of Comparable<String>.compareTo(T). */
public abstract class OverrideBridgeClashBroken implements Comparable<String> {
  public int compareTo(Object o) {
    return 0;
  }
}
