package oop.override;

/** Not allowed: @Override on a method whose "parent" version is private (not inherited). */
class PrivBase {
  private void helper() {}
}

public class OverridePrivateAnnBroken extends PrivBase {
  @Override
  void helper() {}
}
