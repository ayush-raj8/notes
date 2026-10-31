package oop.immut;

/** Intentionally invalid: a final field cannot be reassigned outside a constructor. */
public final class ImmutFinalFieldSetterBroken {
  private final int x;

  public ImmutFinalFieldSetterBroken(int x) {
    this.x = x;
  }

  public void setX(int x) {
    this.x = x;
  }
}
