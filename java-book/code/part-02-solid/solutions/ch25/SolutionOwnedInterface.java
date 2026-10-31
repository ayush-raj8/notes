package solid.dip.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 4: the domain defines the interface it needs; the detail implements it. */
public final class SolutionOwnedInterface {
  // domain side
  interface Alerts {
    void raise(String message);
  }

  record Monitor(Alerts alerts) {
    void check(int temperature) {
      if (temperature > 80) {
        alerts.raise("too hot: " + temperature);
      }
    }
  }

  // detail side
  static final class ListAlerts implements Alerts {
    final List<String> raised = new ArrayList<>();

    public void raise(String message) {
      raised.add(message);
    }
  }

  public static void main(String[] args) {
    ListAlerts alerts = new ListAlerts();
    Monitor m = new Monitor(alerts);
    m.check(70);
    m.check(95);
    System.out.println(alerts.raised);
  }
}
