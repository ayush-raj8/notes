package oop.override;

class CtorBase {
  CtorBase(int x) {}
}

/** Not allowed: a "constructor" named after the parent is just a method without a return type. */
public class OverrideCtorBroken extends CtorBase {
  CtorBase(int x) {}
}
