package oop.abstracts;

class EpAccount {
  private long cents = 100;
}

/** Not allowed: reading a private field from another class. */
public class AbsEncapsulationPrivateBroken {
  public static void main(String[] args) {
    EpAccount a = new EpAccount();
    System.out.println(a.cents);
  }
}
