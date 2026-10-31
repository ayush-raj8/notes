package patterns.behavioral.observer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/** JDK observers: {@code java.beans} listeners and {@code java.util.concurrent.Flow}. */
public final class JdkObserverDemo {
  // ANCHOR: jdk-bean
  static final class Gauge {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int level;

    void addListener(PropertyChangeListener l) {
      pcs.addPropertyChangeListener(l);
    }

    void setLevel(int v) {
      int old = level;
      level = v;
      pcs.firePropertyChange("level", old, v);
    }
  }
  // ANCHOR_END: jdk-bean

  // ANCHOR: jdk-demo
  public static void main(String[] args) {
    Gauge g = new Gauge();
    g.addListener(e -> System.out.println(e.getPropertyName() + " " + e.getOldValue() + " -> " + e.getNewValue()));
    g.setLevel(5);
    g.setLevel(5);
    g.setLevel(8);

    try (SubmissionPublisher<String> pub = new SubmissionPublisher<>(Runnable::run, 8)) {
      pub.subscribe(
          new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription s) {
              s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String item) {
              System.out.println("flow " + item);
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onComplete() {
              System.out.println("flow done");
            }
          });
      pub.submit("a");
      pub.submit("b");
    }
  }
  // ANCHOR_END: jdk-demo
}
