package oop.uml;

// ANCHOR: state-demo
/** State diagram -> code: states are an enum, labelled transitions are a total function State x Event. */
public final class StateDemo {
  private StateDemo() {}

  enum State { NEW, PAID, SHIPPED, DELIVERED, CANCELLED }

  enum Event { PAY, SHIP, DELIVER, CANCEL }

  static State next(State s, Event e) {
    return switch (s) {
      case NEW -> switch (e) {
        case PAY -> State.PAID;
        case CANCEL -> State.CANCELLED;
        default -> throw illegal(s, e);
      };
      case PAID -> switch (e) {
        case SHIP -> State.SHIPPED;
        case CANCEL -> State.CANCELLED;
        default -> throw illegal(s, e);
      };
      case SHIPPED -> {
        if (e == Event.DELIVER) yield State.DELIVERED;
        throw illegal(s, e);
      }
      case DELIVERED, CANCELLED -> throw illegal(s, e); // final states: no outgoing transition
    };
  }

  private static IllegalStateException illegal(State s, Event e) {
    return new IllegalStateException(e + " not allowed in " + s);
  }

  public static void main(String[] args) {
    State s = State.NEW;
    for (Event e : new Event[] {Event.PAY, Event.SHIP, Event.DELIVER}) {
      State n = next(s, e);
      System.out.println(s + " --" + e + "--> " + n);
      s = n;
    }
    try {
      next(State.SHIPPED, Event.CANCEL);
    } catch (IllegalStateException ex) {
      System.out.println(ex.getMessage());
    }
  }
}
// ANCHOR_END: state-demo
