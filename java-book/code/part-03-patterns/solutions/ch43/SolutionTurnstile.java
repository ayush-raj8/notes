package solutions.ch43;

/** Exercise 1: turnstile with LOCKED / UNLOCKED state objects. */
public final class SolutionTurnstile {
  interface State {
    State coin();

    State push();

    String name();
  }

  static final State LOCKED =
      new State() {
        public State coin() {
          return UNLOCKED;
        }

        public State push() {
          return this;
        }

        public String name() {
          return "LOCKED";
        }
      };

  static final State UNLOCKED =
      new State() {
        public State coin() {
          return this;
        }

        public State push() {
          return LOCKED;
        }

        public String name() {
          return "UNLOCKED";
        }
      };

  public static void main(String[] args) {
    State s = LOCKED;
    s = s.push();
    System.out.println(s.name());
    s = s.coin();
    System.out.println(s.name());
    s = s.coin();
    System.out.println(s.name());
    s = s.push();
    System.out.println(s.name());
  }
}
