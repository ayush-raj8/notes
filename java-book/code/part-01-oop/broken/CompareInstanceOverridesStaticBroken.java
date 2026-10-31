package oop.compare;

/** An instance method cannot override a static method. */
class IsParent {
  static void run() {}
}

public class CompareInstanceOverridesStaticBroken extends IsParent {
  void run() {}
}
