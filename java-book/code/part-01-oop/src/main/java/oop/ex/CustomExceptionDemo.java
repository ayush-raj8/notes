package oop.ex;

// ANCHOR: custom-demo
public final class CustomExceptionDemo {
  static int balance = 50;

  static void withdraw(int amount) throws InsufficientFundsException {
    if (amount > balance) {
      throw new InsufficientFundsException("need " + amount + " have " + balance, amount - balance);
    }
    balance -= amount;
  }

  public static void main(String[] args) {
    try {
      withdraw(20);
      withdraw(100);
    } catch (InsufficientFundsException e) {
      System.out.println(e.getMessage());
      System.out.println(e.shortBy());
      System.out.println(e);
    }
  }
}
// ANCHOR_END: custom-demo
