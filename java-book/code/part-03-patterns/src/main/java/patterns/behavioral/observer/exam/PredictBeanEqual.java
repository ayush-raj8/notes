package patterns.behavioral.observer.exam;

import java.beans.PropertyChangeSupport;

// ANCHOR: predict-bean
public final class PredictBeanEqual {
  public static void main(String[] args) {
    PropertyChangeSupport pcs = new PropertyChangeSupport(new Object());
    pcs.addPropertyChangeListener(e -> System.out.println("fired " + e.getNewValue()));
    pcs.firePropertyChange("n", 1, 1);
    pcs.firePropertyChange("n", 1, 2);
    pcs.firePropertyChange("n", "a", new String("a"));
    pcs.firePropertyChange("n", null, null);
    pcs.firePropertyChange("n", null, "x");
  }
}
// ANCHOR_END: predict-bean
