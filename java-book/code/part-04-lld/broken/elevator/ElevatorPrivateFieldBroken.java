import lld.elevator.Elevator;

public class ElevatorPrivateFieldBroken {
  public static void main(String[] args) {
    Elevator e = new Elevator(0, 0, 9);
    e.floor = 5;
    System.out.println(e);
  }
}
