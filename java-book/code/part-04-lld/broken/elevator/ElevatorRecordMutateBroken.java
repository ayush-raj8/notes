import lld.elevator.Direction;
import lld.elevator.HallCall;

public class ElevatorRecordMutateBroken {
  public static void main(String[] args) {
    HallCall call = new HallCall(3, Direction.UP);
    call.floor = 4;
    System.out.println(call);
  }
}
