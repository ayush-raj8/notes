package lld.atm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ANCHOR: in-memory-bank
public final class InMemoryBank implements Bank {
  private static final class Account {
    private final String pin;
    private long balance;

    Account(String pin, long balance) {
      this.pin = pin;
      this.balance = balance;
    }
  }

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  public void open(String card, String pin, long opening) {
    accounts.put(card, new Account(pin, opening));
  }

  private Account account(String card) {
    Account a = accounts.get(card);
    if (a == null) {
      throw new IllegalArgumentException("unknown card " + card);
    }
    return a;
  }

  @Override
  public boolean verifyPin(String card, String pin) {
    return account(card).pin.equals(pin);
  }

  @Override
  public long balance(String card) {
    Account a = account(card);
    synchronized (a) {
      return a.balance;
    }
  }

  @Override
  public void debit(String card, long amount) throws InsufficientFundsException {
    Account a = account(card);
    synchronized (a) {
      if (a.balance < amount) {
        throw new InsufficientFundsException(a.balance, amount);
      }
      a.balance -= amount;
    }
  }

  @Override
  public void credit(String card, long amount) {
    Account a = account(card);
    synchronized (a) {
      a.balance += amount;
    }
  }
}
// ANCHOR_END: in-memory-bank
