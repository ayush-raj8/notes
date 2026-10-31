package solid.lsp.exam;

// ANCHOR: predict-covariant
public final class PredictCovariantDispatch {
  static class Animal {
    Animal mate() {
      return new Animal();
    }
  }

  static class Dog extends Animal {
    @Override
    Dog mate() {
      return new Dog();
    }
  }

  static String describe(Animal a) {
    return "animal";
  }

  static String describe(Dog d) {
    return "dog";
  }

  public static void main(String[] args) {
    Animal a = new Dog();
    System.out.println(a.mate().getClass().getSimpleName());
    System.out.println(describe(a.mate()));
    System.out.println(describe(new Dog().mate()));
  }
}
// ANCHOR_END: predict-covariant
