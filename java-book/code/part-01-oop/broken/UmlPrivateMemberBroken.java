package oop.uml;

/** Intentionally invalid: a "-" member cannot be used from another top-level class. */
class UmlPrivateOwner {
  private int secret = 1;
}

public final class UmlPrivateMemberBroken {
  public static void main(String[] args) {
    System.out.println(new UmlPrivateOwner().secret);
  }
}
