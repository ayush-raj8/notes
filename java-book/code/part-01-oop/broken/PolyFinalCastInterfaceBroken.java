package oop.poly;

/** A final class that does not implement the interface can never be an instance of it. */
final class FcAnimal {}

public class PolyFinalCastInterfaceBroken {
  public static void main(String[] args) {
    FcAnimal a = new FcAnimal();
    Runnable r = (Runnable) a;
    r.run();
  }
}
