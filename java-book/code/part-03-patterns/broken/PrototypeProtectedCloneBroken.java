package patterns.creational.prototype;

/** Intentionally invalid: Object.clone() is protected. */
public final class PrototypeProtectedCloneBroken {
  public static void main(String[] args) throws Exception {
    Object o = new Object();
    Object copy = o.clone();
  }
}
