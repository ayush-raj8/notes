package patterns.behavioral.state.exam;

import patterns.behavioral.state.TrafficLight;

// ANCHOR: predict-enum-cycle
public final class PredictEnumCycle {
  public static void main(String[] args) {
    TrafficLight l = TrafficLight.GREEN;
    int total = 0;
    for (int i = 0; i < 4; i++) {
      total += l.seconds();
      l = l.next();
    }
    System.out.println(l + " " + total);
    System.out.println(TrafficLight.RED.getClass() == TrafficLight.class);
    System.out.println(TrafficLight.valueOf("YELLOW").ordinal());
  }
}
// ANCHOR_END: predict-enum-cycle
