package patterns.creational.prototype;

/** Intentionally invalid: super.clone() declares a checked exception. */
public final class PrototypeUncheckedBroken implements Cloneable {
  @Override
  public PrototypeUncheckedBroken clone() {
    return (PrototypeUncheckedBroken) super.clone();
  }
}
