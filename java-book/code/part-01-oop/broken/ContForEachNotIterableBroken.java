package oop.contracts;

public final class ContForEachNotIterableBroken {
  static final class Bag {
    int[] items = {1, 2, 3};
  }

  public static void main(String[] args) {
    Bag bag = new Bag();
    for (int x : bag) {
      System.out.println(x);
    }
  }
}
