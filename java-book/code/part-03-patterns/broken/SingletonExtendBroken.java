package patterns.creational.singleton;

/** Intentionally invalid: subclass of a class whose only constructor is private. */
class PrivateCtorBase {
  private PrivateCtorBase() {}
}

class PrivateCtorChild extends PrivateCtorBase {}

public final class SingletonExtendBroken {}
