package patterns.behavioral.state;

public final class EnumStateDemo {
  // ANCHOR: enum-demo
  public static void main(String[] args) {
    TrafficLight l = TrafficLight.RED;
    for (int i = 0; i < 4; i++) {
      System.out.println(l + " " + l.seconds() + "s");
      l = l.next();
    }
    System.out.println(TrafficLight.RED.getClass() == TrafficLight.class);
    System.out.println(TrafficLight.RED.getDeclaringClass() == TrafficLight.class);
  }
  // ANCHOR_END: enum-demo
}
