package oop.abstracts;

// ANCHOR: encapsulation-demo
class SafeAccount {
  private long cents;

  SafeAccount(long openingCents) {
    if (openingCents < 0) {
      throw new IllegalArgumentException("opening balance < 0");
    }
    this.cents = openingCents;
  }

  void deposit(long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("deposit must be > 0");
    }
    cents += amount;
  }

  boolean withdraw(long amount) {
    if (amount <= 0 || amount > cents) {
      return false;
    }
    cents -= amount;
    return true;
  }

  long balance() {
    return cents;
  }
}

/** Private state + methods that enforce the invariant (balance never negative). */
public final class EncapsulationDemo {
  public static void main(String[] args) {
    SafeAccount a = new SafeAccount(500);
    a.deposit(250);
    System.out.println(a.balance());
    System.out.println(a.withdraw(1000));
    System.out.println(a.withdraw(300));
    System.out.println(a.balance());
    try {
      a.deposit(-5);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: encapsulation-demo
