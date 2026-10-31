package oop.immut;

/** Intentionally invalid: a compact constructor must not assign the fields. */
public final class ImmutCompactAssignBroken {
  record Name(String value) {
    Name {
      this.value = value.trim();
    }
  }
}
