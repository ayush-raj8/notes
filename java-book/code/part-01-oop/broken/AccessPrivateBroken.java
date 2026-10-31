package oop.access;

/** Intentionally invalid: private member read from another class (same package). */
public final class AccessPrivateBroken {
  public static void main(String[] args) {
    AccessLevels a = new AccessLevels();
    System.out.println(a.priv);
  }
}
