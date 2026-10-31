package oop.compare;

/** @Override on a method that only overloads. */
class OaParent {
  void pick(Object o) {}
}

public class CompareOverrideAnnOverloadBroken extends OaParent {
  @Override
  void pick(String s) {}
}
