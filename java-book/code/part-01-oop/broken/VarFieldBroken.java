package oop.memory;

/**
 * Intentionally invalid: {@code var} is not allowed for fields.
 * Excluded from Maven compile.
 */
public final class VarFieldBroken {
  private var count = 0;

  public int inc() {
    return ++count;
  }
}
