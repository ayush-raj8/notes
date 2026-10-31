package lld.elevator;

// ANCHOR: elevator-demo
public final class ElevatorDemo {
  private ElevatorDemo() {}

  public static void main(String[] args) {
    ElevatorController c = new ElevatorController(0, 9, 2, new NearestCarStrategy());
    c.requestCabin(0, 5);
    int answered = c.requestHall(new HallCall(3, Direction.DOWN));
    System.out.println("hall call at 3 answered by E" + answered);
    for (int t = 1; t <= 6; t++) {
      var events = c.tick();
      System.out.println("t" + t + " " + c.elevators() + " " + events);
    }
  }
}
// ANCHOR_END: elevator-demo
