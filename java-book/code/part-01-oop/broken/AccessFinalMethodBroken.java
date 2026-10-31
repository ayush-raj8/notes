package oop.access;

/** Intentionally invalid: overriding a final method. */
class FinalMethodParent {
  final void run() {}
}

class FinalMethodChild extends FinalMethodParent {
  @Override
  void run() {}
}

public final class AccessFinalMethodBroken {}
