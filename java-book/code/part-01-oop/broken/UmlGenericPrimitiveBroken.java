package oop.uml;

import java.util.List;

/** Intentionally invalid: a type argument (Repository~T~) must be a reference type. */
public final class UmlGenericPrimitiveBroken {
  interface Repository<T> {
    void save(T item);
  }

  static List<int> numbers;
}
