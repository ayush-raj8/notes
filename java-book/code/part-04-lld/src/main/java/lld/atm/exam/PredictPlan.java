package lld.atm.exam;

import lld.atm.CashDispenser;

// ANCHOR: predict-plan
public class PredictPlan {
  public static void main(String[] args) {
    CashDispenser d = new CashDispenser(1, 3, 0);
    System.out.println(d.plan(600));
    System.out.println(d.plan(700));
    System.out.println(d.plan(150));
    System.out.println(d.plan(1100));
    System.out.println(d.total());
  }
}
// ANCHOR_END: predict-plan
