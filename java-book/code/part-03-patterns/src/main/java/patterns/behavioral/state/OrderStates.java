package patterns.behavioral.state;

// ANCHOR: states
/** Concrete states as stateless singletons; each overrides only its legal actions. */
public final class OrderStates {
  private OrderStates() {}

  public static final OrderState NEW =
      new OrderState() {
        public String name() {
          return "NEW";
        }

        @Override
        public void pay(Order o) {
          o.setState(PAID);
        }

        @Override
        public void cancel(Order o) {
          o.setState(CANCELLED);
        }
      };

  public static final OrderState PAID =
      new OrderState() {
        public String name() {
          return "PAID";
        }

        @Override
        public void ship(Order o) {
          o.setState(SHIPPED);
        }

        @Override
        public void cancel(Order o) {
          o.setState(CANCELLED);
        }
      };

  public static final OrderState SHIPPED =
      new OrderState() {
        public String name() {
          return "SHIPPED";
        }
      };

  public static final OrderState CANCELLED =
      new OrderState() {
        public String name() {
          return "CANCELLED";
        }
      };
}
// ANCHOR_END: states
