package patterns.creational.singleton;

/** Intentionally invalid: enums cannot be instantiated with new. */
public final class SingletonNewEnumBroken {
  public static void main(String[] args) {
    EnumSingleton s = new EnumSingleton();
  }
}
