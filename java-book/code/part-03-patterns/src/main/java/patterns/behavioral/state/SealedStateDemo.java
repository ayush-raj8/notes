package patterns.behavioral.state;

/** Java 21: sealed states + records + pattern-matching switch = a checked transition table. */
public final class SealedStateDemo {
  // ANCHOR: sealed-state
  public sealed interface Conn permits Closed, Open {}

  public record Closed() implements Conn {}

  public record Open(int sent) implements Conn {}

  public enum Event {
    OPEN,
    SEND,
    CLOSE
  }

  public static Conn next(Conn c, Event e) {
    return switch (c) {
      case Closed x ->
          switch (e) {
            case OPEN -> new Open(0);
            case SEND, CLOSE -> x;
          };
      case Open o ->
          switch (e) {
            case OPEN -> o;
            case SEND -> new Open(o.sent() + 1);
            case CLOSE -> new Closed();
          };
    };
  }
  // ANCHOR_END: sealed-state

  // ANCHOR: sealed-demo
  public static void main(String[] args) {
    Conn c = new Closed();
    for (Event e : new Event[] {Event.SEND, Event.OPEN, Event.SEND, Event.SEND, Event.CLOSE}) {
      c = next(c, e);
      System.out.println(e + " -> " + c);
    }
  }
  // ANCHOR_END: sealed-demo
}
