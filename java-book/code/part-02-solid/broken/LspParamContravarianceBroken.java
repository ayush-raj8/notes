package solid.lsp;

/** Contravariant parameters are sound in theory, but Java treats a wider parameter as an overload. */
public final class LspParamContravarianceBroken {
  static class Animal {}

  static class Dog extends Animal {}

  static class Kennel {
    void accept(Dog d) {}
  }

  static class OpenKennel extends Kennel {
    @Override
    void accept(Animal a) {}
  }
}
