package patterns.behavioral.chain.exam;

import patterns.behavioral.chain.Approver;
import patterns.behavioral.chain.Clerk;
import patterns.behavioral.chain.Director;
import patterns.behavioral.chain.Manager;

// ANCHOR: predict-order-matters
public final class PredictOrderMatters {
  public static void main(String[] args) {
    Approver reversed = new Director();
    reversed.then(new Manager()).then(new Clerk());
    System.out.println(reversed.approve(10));

    Approver split = new Clerk();
    Approver mgr = split.then(new Manager());
    System.out.println(split.approve(500));
    System.out.println(mgr.approve(50));
  }
}
// ANCHOR_END: predict-order-matters
