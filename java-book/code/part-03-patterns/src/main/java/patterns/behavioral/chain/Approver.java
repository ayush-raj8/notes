package patterns.behavioral.chain;

// ANCHOR: approver
/** Handler role: handles the request itself or passes it to the next handler. */
public abstract class Approver {
  private Approver next;

  /** Links the next handler and returns it, so calls can be chained fluently. */
  public Approver then(Approver n) {
    this.next = n;
    return n;
  }

  public final String approve(int amount) {
    if (amount <= limit()) {
      return name() + " approved " + amount;
    }
    if (next == null) {
      return "nobody can approve " + amount;
    }
    return next.approve(amount);
  }

  protected abstract int limit();

  protected abstract String name();
}
// ANCHOR_END: approver
