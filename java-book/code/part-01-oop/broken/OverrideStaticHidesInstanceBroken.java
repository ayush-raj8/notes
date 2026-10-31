package oop.override;

/** Not allowed: a static method cannot hide an instance method. */
class InstBase {
  void tick() {}
}

public class OverrideStaticHidesInstanceBroken extends InstBase {
  static void tick() {}
}
