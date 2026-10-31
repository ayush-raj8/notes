package solid.srp.solutions;

import solid.srp.parts.ReceiptNotifier;

/** Exercise 4: implement the notifier with a lambda and count how often it fires. */
public final class SolutionNotifierLambda {
  public static void main(String[] args) {
    int[] count = {0};
    ReceiptNotifier counting = (id, cents) -> count[0]++;
    counting.send("A", 1);
    counting.send("B", 2);
    System.out.println("sent=" + count[0]);
  }
}
