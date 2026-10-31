package oop.ctors;

// ANCHOR: account-ctors
/** Parameterized, private, and copy constructors on one type. */
public final class Account {
  private final String id;
  private int balance;

  public Account(String id, int balance) {
    this.id = id;
    this.balance = balance;
  }

  /** Copy constructor — new object, same field values (shallow for references). */
  public Account(Account other) {
    this(other.id, other.balance);
  }

  private Account(String id) {
    this(id, 0);
  }

  public static Account zero(String id) {
    return new Account(id);
  }

  public String id() {
    return id;
  }

  public int balance() {
    return balance;
  }

  public void deposit(int amount) {
    balance += amount;
  }

  @Override
  public String toString() {
    return id + ":" + balance;
  }
}
// ANCHOR_END: account-ctors
