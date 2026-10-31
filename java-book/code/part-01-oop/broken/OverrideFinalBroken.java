package oop.override;

/** Not allowed: overriding a final method. */
class FinalBase {
  final void lock() {}
}

public class OverrideFinalBroken extends FinalBase {
  @Override
  void lock() {}
}
