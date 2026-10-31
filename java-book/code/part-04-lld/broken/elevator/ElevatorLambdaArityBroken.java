import lld.elevator.DispatchStrategy;

public class ElevatorLambdaArityBroken {
  public static void main(String[] args) {
    DispatchStrategy s = list -> list.get(0);
    System.out.println(s);
  }
}
