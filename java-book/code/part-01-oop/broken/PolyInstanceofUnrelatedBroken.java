package oop.poly;

/** instanceof with unrelated class types is rejected. */
class IuDog {}

public class PolyInstanceofUnrelatedBroken {
  public static void main(String[] args) {
    IuDog d = new IuDog();
    System.out.println(d instanceof String);
  }
}
