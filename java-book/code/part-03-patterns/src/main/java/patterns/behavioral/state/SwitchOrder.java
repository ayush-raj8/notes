package patterns.behavioral.state;

/** The problem State solves: every action switches on the status flag. */
public final class SwitchOrder {
  enum Status {
    NEW,
    PAID,
    SHIPPED,
    CANCELLED
  }

  private Status status = Status.NEW;

  // ANCHOR: switch-order
  void pay() {
    switch (status) {
      case NEW -> status = Status.PAID;
      default -> throw new IllegalStateException("cannot pay in " + status);
    }
  }

  void ship() {
    switch (status) {
      case PAID -> status = Status.SHIPPED;
      default -> throw new IllegalStateException("cannot ship in " + status);
    }
  }

  void cancel() {
    switch (status) {
      case NEW, PAID -> status = Status.CANCELLED;
      default -> throw new IllegalStateException("cannot cancel in " + status);
    }
  }
  // ANCHOR_END: switch-order

  public static void main(String[] args) {
    SwitchOrder o = new SwitchOrder();
    o.pay();
    o.ship();
    try {
      o.cancel();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
