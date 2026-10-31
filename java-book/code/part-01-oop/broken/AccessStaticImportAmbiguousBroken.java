package oop.access;

import static oop.access.other.Consts.*;
import static oop.access.other.OtherConsts.*;

/** Intentionally invalid: two on-demand static imports both supply {@code twice(int)}. */
public final class AccessStaticImportAmbiguousBroken {
  public static void main(String[] args) {
    System.out.println(twice(1));
  }
}
