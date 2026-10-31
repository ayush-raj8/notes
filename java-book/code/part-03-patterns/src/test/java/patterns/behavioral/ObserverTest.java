package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import patterns.behavioral.observer.NaivePriceSubject;
import patterns.behavioral.observer.PriceObserver;
import patterns.behavioral.observer.PriceSubject;
import patterns.behavioral.observer.Thermometer;

class ObserverTest {
  @Test
  void notifiesInRegistrationOrder() {
    List<String> seen = new ArrayList<>();
    PriceSubject s = new PriceSubject();
    s.attach((sym, p) -> seen.add("A" + p));
    s.attach((sym, p) -> seen.add("B" + p));
    s.publish("X", 1);
    assertEquals(List.of("A1", "B1"), seen);
  }

  @Test
  void selfDetachSafeWithSnapshot() {
    List<String> seen = new ArrayList<>();
    PriceSubject s = new PriceSubject();
    PriceObserver[] a = new PriceObserver[1];
    a[0] =
        (sym, p) -> {
          seen.add("A");
          s.detach(a[0]);
        };
    s.attach(a[0]);
    s.attach((sym, p) -> seen.add("B"));
    s.attach((sym, p) -> seen.add("C"));
    s.publish("X", 1);
    s.publish("X", 2);
    assertEquals(List.of("A", "B", "C", "B", "C"), seen);
  }

  @Test
  void naiveSubjectBreaksWhenObserverDetaches() {
    NaivePriceSubject s = new NaivePriceSubject();
    PriceObserver[] a = new PriceObserver[1];
    a[0] = (sym, p) -> s.detach(a[0]);
    s.attach(a[0]);
    s.attach((sym, p) -> {});
    s.attach((sym, p) -> {});
    assertThrows(ConcurrentModificationException.class, () -> s.publish("X", 1));
  }

  @Test
  void pullObserverReadsSubject() {
    Thermometer t = new Thermometer();
    List<Integer> seen = new ArrayList<>();
    t.watch(x -> seen.add(x.celsius()));
    t.set(5);
    t.set(9);
    assertEquals(List.of(5, 9), seen);
    assertEquals(2, t.changes());
  }
}
