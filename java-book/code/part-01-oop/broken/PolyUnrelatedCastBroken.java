package oop.poly;

/** A cast between unrelated classes is rejected at compile time. */
class UcDog {}

public class PolyUnrelatedCastBroken {
  public static void main(String[] args) {
    UcDog d = new UcDog();
    String s = (String) d;
    System.out.println(s);
  }
}
