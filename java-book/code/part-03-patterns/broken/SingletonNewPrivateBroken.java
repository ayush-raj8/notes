package patterns.creational.singleton;

/** Intentionally invalid: private constructor used from another class. */
public final class SingletonNewPrivateBroken {
  public static void main(String[] args) {
    HolderSingleton s = new HolderSingleton();
  }
}
