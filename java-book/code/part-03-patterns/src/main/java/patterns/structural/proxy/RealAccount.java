package patterns.structural.proxy;

public final class RealAccount implements Account {
  private int balance;

  public RealAccount(int balance) {
    this.balance = balance;
  }

  @Override
  public int balance() {
    return balance;
  }

  @Override
  public void withdraw(int amount) {
    balance -= amount;
  }
}
