package oop.uml.exam;

// ANCHOR: predict-door-state
public final class PredictDoorState {
  enum Door {
    CLOSED, OPEN, LOCKED;

    Door on(String event) {
      return switch (this) {
        case CLOSED -> switch (event) {
          case "open" -> OPEN;
          case "lock" -> LOCKED;
          default -> this;
        };
        case OPEN -> event.equals("close") ? CLOSED : this;
        case LOCKED -> event.equals("unlock") ? CLOSED : this;
      };
    }
  }

  public static void main(String[] args) {
    Door d = Door.CLOSED;
    for (String e : new String[] {"open", "lock", "close", "lock", "open", "unlock"}) {
      d = d.on(e);
      System.out.print(d + " ");
    }
    System.out.println();
  }
}
// ANCHOR_END: predict-door-state
