package oop.access;

/** Intentionally invalid: instance field used from a static method. */
public class AccessStaticThisBroken {
  private int n = 1;

  public static void main(String[] args) {
    System.out.println(n);
  }
}
