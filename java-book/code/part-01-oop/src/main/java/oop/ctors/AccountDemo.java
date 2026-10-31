package oop.ctors;

// ANCHOR: account-demo
public final class AccountDemo {
  public static void main(String[] args) {
    Account a = new Account("A1", 100);
    Account copy = new Account(a);
    Account z = Account.zero("Z");
    a.deposit(50);
    System.out.println(a);
    System.out.println(copy);
    System.out.println(z);
  }
}
// ANCHOR_END: account-demo
