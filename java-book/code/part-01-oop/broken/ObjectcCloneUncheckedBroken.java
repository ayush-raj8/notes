package oop.objectc;

/** Intentionally invalid: super.clone() declares a checked exception. */
public final class ObjectcCloneUncheckedBroken implements Cloneable {
  @Override
  public ObjectcCloneUncheckedBroken clone() {
    return (ObjectcCloneUncheckedBroken) super.clone();
  }
}
