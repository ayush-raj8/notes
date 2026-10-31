package oop.generics;

import java.util.List;

/** Intentionally invalid: the type argument cannot be checked at run time. */
public final class GenInstanceofBroken {
  static boolean test(Object o) {
    return o instanceof List<String>;
  }
}
