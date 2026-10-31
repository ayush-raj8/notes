package patterns.structural.proxy;

// ANCHOR: protected-account
/** Protection proxy: same interface, checks the caller's role first. */
public final class ProtectedAccount implements Account {
  private final Account target;
  private final String role;

  public ProtectedAccount(Account target, String role) {
    this.target = target;
    this.role = role;
  }

  @Override
  public int balance() {
    return target.balance();
  }

  @Override
  public void withdraw(int amount) {
    if (!"owner".equals(role)) {
      throw new SecurityException("role " + role + " may not withdraw");
    }
    target.withdraw(amount);
  }
}
// ANCHOR_END: protected-account
