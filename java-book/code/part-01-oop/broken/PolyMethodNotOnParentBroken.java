package oop.poly;

/** The compile-time type decides which members are visible. */
class MpAnimal {}

class MpDog extends MpAnimal {
  String fetch() {
    return "fetch";
  }
}

public class PolyMethodNotOnParentBroken {
  public static void main(String[] args) {
    MpAnimal a = new MpDog();
    System.out.println(a.fetch());
  }
}
