package solid.principles.solutions;

/** Exercise 4: a tell-style transfer; the rules stay inside Account, the caller only gives orders. */
public final class SolutionTellTransfer {
  static final class Account {
    private long balance;

    Account(long balance) {
      this.balance = balance;
    }

    boolean withdraw(long amount) {
      if (amount <= 0 || amount > balance) {
        return false;
      }
      balance -= amount;
      return true;
    }

    void deposit(long amount) {
      if (amount > 0) {
        balance += amount;
      }
    }

    long balance() {
      return balance;
    }
  }

  static boolean transfer(Account from, Account to, long amount) {
    if (!from.withdraw(amount)) {
      return false;
    }
    to.deposit(amount);
    return true;
  }

  public static void main(String[] args) {
    Account a = new Account(100);
    Account b = new Account(0);
    System.out.println(transfer(a, b, 30) + " " + a.balance() + " " + b.balance());
    System.out.println(transfer(a, b, 300) + " " + a.balance() + " " + b.balance());
  }
}
