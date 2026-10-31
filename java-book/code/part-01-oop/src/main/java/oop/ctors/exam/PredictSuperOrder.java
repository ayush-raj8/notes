package oop.ctors.exam;

// ANCHOR: predict-super-order
class PredictAnimal {
  PredictAnimal() {
    System.out.println("Animal");
  }
}

class PredictDog extends PredictAnimal {
  PredictDog() {
    System.out.println("Dog");
  }
}

public final class PredictSuperOrder {
  public static void main(String[] args) {
    new PredictDog();
  }
}
// ANCHOR_END: predict-super-order
