package oop.poly;

/** Downcast needs an explicit cast. */
class DnAnimal {}

class DnDog extends DnAnimal {}

public class PolyDowncastNoCastBroken {
  public static void main(String[] args) {
    DnAnimal a = new DnDog();
    DnDog d = a;
    System.out.println(d);
  }
}
