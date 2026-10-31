package patterns.creational.prototype;

/** Intentionally invalid: a primitive return type cannot override Object.clone(). */
public final class PrototypeWrongReturnBroken implements Cloneable {
  @Override
  public int clone() {
    return 0;
  }
}
