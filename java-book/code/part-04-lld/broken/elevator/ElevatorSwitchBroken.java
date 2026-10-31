import lld.elevator.Direction;

public class ElevatorSwitchBroken {
  static int delta(Direction d) {
    return switch (d) {
      case UP -> 1;
      case DOWN -> -1;
    };
  }

  public static void main(String[] args) {
    System.out.println(delta(Direction.UP));
  }
}
