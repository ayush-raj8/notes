package oop.uml.solutions;

/** State diagram -> code: unlisted (state, event) pairs leave the state unchanged. */
public final class SolutionDoorState {
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
      Door n = d.on(e);
      System.out.println(d + " --" + e + "--> " + n);
      d = n;
    }
  }
}
