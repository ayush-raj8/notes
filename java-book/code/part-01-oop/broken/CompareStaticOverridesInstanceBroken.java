package oop.compare;

/** A static method cannot hide an instance method. */
class SoParent {
  void run() {}
}

public class CompareStaticOverridesInstanceBroken extends SoParent {
  static void run() {}
}
