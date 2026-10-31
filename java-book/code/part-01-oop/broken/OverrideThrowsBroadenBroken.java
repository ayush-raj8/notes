package oop.override;

/** Not allowed: override declares a broader checked exception. */
class ThrowsBase {
  void run() {}
}

public class OverrideThrowsBroadenBroken extends ThrowsBase {
  @Override
  void run() throws Exception {}
}
