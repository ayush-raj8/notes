package patterns.behavioral.state;

// ANCHOR: context
/** Context: holds the current state and delegates every action to it. */
public final class Order {
  private OrderState state = OrderStates.NEW;

  void setState(OrderState next) {
    state = next;
  }

  public OrderState current() {
    return state;
  }

  public String state() {
    return state.name();
  }

  public void pay() {
    state.pay(this);
  }

  public void ship() {
    state.ship(this);
  }

  public void cancel() {
    state.cancel(this);
  }
}
// ANCHOR_END: context
