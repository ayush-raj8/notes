package patterns.behavioral.chain.exam;

import patterns.behavioral.chain.Approver;
import patterns.behavioral.chain.Clerk;
import patterns.behavioral.chain.Director;
import patterns.behavioral.chain.Manager;

// ANCHOR: predict-approve
public final class PredictApprove {
  public static void main(String[] args) {
    Approver a = new Clerk();
    a.then(new Manager()).then(new Director());
    System.out.println(a.approve(100));
    System.out.println(a.approve(101));
    System.out.println(a.approve(10_000));
    System.out.println(a.approve(10_001));
  }
}
// ANCHOR_END: predict-approve
