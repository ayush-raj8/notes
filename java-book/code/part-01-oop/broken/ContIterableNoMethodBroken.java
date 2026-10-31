package oop.contracts;

public final class ContIterableNoMethodBroken implements Iterable<Integer> {
  public static void main(String[] args) {
    for (int x : new ContIterableNoMethodBroken()) {
      System.out.println(x);
    }
  }
}
