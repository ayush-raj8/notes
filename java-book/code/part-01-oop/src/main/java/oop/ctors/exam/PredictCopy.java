package oop.ctors.exam;

import oop.ctors.Account;

// ANCHOR: predict-copy
public final class PredictCopy {
  public static void main(String[] args) {
    Account a = new Account("X", 10);
    Account b = new Account(a);
    a.deposit(5);
    System.out.println(a);
    System.out.println(b);
  }
}
// ANCHOR_END: predict-copy
