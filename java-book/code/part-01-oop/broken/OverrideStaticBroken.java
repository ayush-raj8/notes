package oop.override;

/** Not allowed: an instance method cannot override a static method. */
class StaticBase {
  static void tick() {}
}

public class OverrideStaticBroken extends StaticBase {
  void tick() {}
}
