package oop.immut;

/** Intentionally invalid: enum constants are the only instances. */
public final class ImmutEnumNewBroken {
  enum Level { LOW, HIGH }

  public static void main(String[] args) {
    Level l = new Level();
  }
}
