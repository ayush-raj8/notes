package patterns.structural.flyweight;

/** Intentionally invalid: TreeType is final so nobody can add mutable state by subclassing. */
public final class FlySubclassFinalBroken extends TreeType {
  FlySubclassFinalBroken() {
    super("x", "y");
  }
}
