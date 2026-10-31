package patterns.behavioral.state;

// ANCHOR: enum-state
/** Enum constants with bodies are states; each constant knows its successor. */
public enum TrafficLight {
  RED {
    @Override
    public TrafficLight next() {
      return GREEN;
    }

    @Override
    public int seconds() {
      return 30;
    }
  },
  GREEN {
    @Override
    public TrafficLight next() {
      return YELLOW;
    }

    @Override
    public int seconds() {
      return 25;
    }
  },
  YELLOW {
    @Override
    public TrafficLight next() {
      return RED;
    }

    @Override
    public int seconds() {
      return 5;
    }
  };

  public abstract TrafficLight next();

  public abstract int seconds();
}
// ANCHOR_END: enum-state
