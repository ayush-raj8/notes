package oop.memory;

/**
 * Intentionally invalid: {@code this} is not a writable variable.
 * Excluded from Maven compile.
 */
public final class ThisReassignBroken {
  private int value;

  public ThisReassignBroken(int value) {
    this.value = value;
  }

  public void replaceWith(ThisReassignBroken other) {
    this = other;
  }
}
