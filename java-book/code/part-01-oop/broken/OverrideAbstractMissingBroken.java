package oop.override;

abstract class NeedsBody {
  abstract double area();
}

/** Not allowed: concrete subclass that does not implement the inherited abstract method. */
public class OverrideAbstractMissingBroken extends NeedsBody {}
