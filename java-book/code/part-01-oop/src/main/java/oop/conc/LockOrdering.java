package oop.conc;

// ANCHOR: lock-ordering
/** Deadlock needs a cycle of waits. A fixed global lock order removes the cycle. */
public final class LockOrdering {
  static final class Account {
    final int id;
    int balance;

    Account(int id, int balance) {
      this.id = id;
      this.balance = balance;
    }
  }

  static void transfer(Account from, Account to, int amount) {
    Account first = from.id < to.id ? from : to; // always lock the lower id first
    Account second = first == from ? to : from;
    synchronized (first) {
      synchronized (second) {
        from.balance -= amount;
        to.balance += amount;
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Account a = new Account(1, 1000);
    Account b = new Account(2, 1000);
    Thread t1 = new Thread(() -> { for (int i = 0; i < 10_000; i++) transfer(a, b, 1); });
    Thread t2 = new Thread(() -> { for (int i = 0; i < 10_000; i++) transfer(b, a, 1); });
    t1.start(); t2.start(); t1.join(); t2.join();
    System.out.println("a=" + a.balance + " b=" + b.balance + " sum=" + (a.balance + b.balance));
  }
}
// ANCHOR_END: lock-ordering
