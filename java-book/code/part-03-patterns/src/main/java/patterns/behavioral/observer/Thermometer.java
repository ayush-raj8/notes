package patterns.behavioral.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// ANCHOR: pull-subject
/** Pull model: the notification carries the subject; observers read what they need. */
public class Thermometer {
  private final List<Consumer<Thermometer>> watchers = new ArrayList<>();
  private int celsius;
  private int changes;

  public void watch(Consumer<Thermometer> w) {
    watchers.add(w);
  }

  public void set(int celsius) {
    this.celsius = celsius;
    changes++;
    for (Consumer<Thermometer> w : List.copyOf(watchers)) {
      w.accept(this);
    }
  }

  public int celsius() {
    return celsius;
  }

  public int changes() {
    return changes;
  }
}
// ANCHOR_END: pull-subject
