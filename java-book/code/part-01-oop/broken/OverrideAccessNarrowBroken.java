package oop.override;

/** Not allowed: override with weaker access (package-private vs public). */
class NarrowBase {
  public String id() {
    return "base";
  }
}

public class OverrideAccessNarrowBroken extends NarrowBase {
  @Override
  String id() {
    return "child";
  }
}
