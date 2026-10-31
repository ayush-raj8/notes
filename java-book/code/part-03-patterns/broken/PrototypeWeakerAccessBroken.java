package patterns.creational.prototype;

/** Intentionally invalid: override of protected clone() with package-private access. */
public final class PrototypeWeakerAccessBroken implements Cloneable {
  @Override
  Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
