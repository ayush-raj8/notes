package patterns.creational.singleton;

/** Intentionally invalid: static accessor reads a non-static field. */
public final class SingletonStaticContextBroken {
  private SingletonStaticContextBroken instance = new SingletonStaticContextBroken();

  private SingletonStaticContextBroken() {}

  public static SingletonStaticContextBroken getInstance() {
    return instance;
  }
}
