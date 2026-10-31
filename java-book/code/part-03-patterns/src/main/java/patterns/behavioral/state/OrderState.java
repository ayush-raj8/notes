package patterns.behavioral.state;

// ANCHOR: state-iface
/** State role: every action is illegal unless a concrete state overrides it. */
public interface OrderState {
  String name();

  default void pay(Order o) {
    throw illegal("pay");
  }

  default void ship(Order o) {
    throw illegal("ship");
  }

  default void cancel(Order o) {
    throw illegal("cancel");
  }

  private IllegalStateException illegal(String action) {
    return new IllegalStateException("cannot " + action + " in " + name());
  }
}
// ANCHOR_END: state-iface
