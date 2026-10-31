package oop.generics;

/** Intentionally invalid: a class type parameter belongs to instances, not statics. */
public final class GenStaticTBroken<T> {
  static T cache;
}
