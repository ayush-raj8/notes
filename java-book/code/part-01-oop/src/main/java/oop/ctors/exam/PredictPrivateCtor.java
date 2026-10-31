package oop.ctors.exam;

import oop.ctors.Account;

// ANCHOR: predict-private-ctor
public final class PredictPrivateCtor {
  public static void main(String[] args) {
    Account z = Account.zero("Z");
    System.out.println(z);
  }
}
// ANCHOR_END: predict-private-ctor
