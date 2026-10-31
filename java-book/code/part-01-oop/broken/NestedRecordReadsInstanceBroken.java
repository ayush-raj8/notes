package oop.nested;

/** Intentionally invalid: a nested record is implicitly static and cannot read outer instance state. */
public final class NestedRecordReadsInstanceBroken {
  private int base = 10;

  record Offset(int d) {
    int total() {
      return base + d;
    }
  }
}
