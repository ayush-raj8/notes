package patterns.behavioral.chain;

public final class ChainDemo {
  // ANCHOR: chain-demo
  public static void main(String[] args) {
    Approver first = new Clerk();
    first.then(new Manager()).then(new Director());
    for (int amount : new int[] {50, 100, 101, 5_000, 50_000}) {
      System.out.println(first.approve(amount));
    }
  }
  // ANCHOR_END: chain-demo
}
