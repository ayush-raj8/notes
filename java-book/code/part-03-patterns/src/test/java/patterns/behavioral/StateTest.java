package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import patterns.behavioral.state.LogSession;
import patterns.behavioral.state.Order;
import patterns.behavioral.state.OrderStates;
import patterns.behavioral.state.SealedStateDemo;
import patterns.behavioral.state.TrafficLight;

class StateTest {
  @Test
  void happyPath() {
    Order o = new Order();
    o.pay();
    o.ship();
    assertEquals("SHIPPED", o.state());
  }

  @Test
  void illegalTransitionKeepsState() {
    Order o = new Order();
    IllegalStateException e = assertThrows(IllegalStateException.class, o::ship);
    assertEquals("cannot ship in NEW", e.getMessage());
    assertEquals("NEW", o.state());
  }

  @Test
  void statesAreShared() {
    Order a = new Order();
    Order b = new Order();
    assertSame(a.current(), b.current());
    a.pay();
    assertSame(OrderStates.PAID, a.current());
  }

  @Test
  void enumCycles() {
    assertEquals(TrafficLight.RED, TrafficLight.RED.next().next().next());
  }

  @Test
  void sealedTransitions() {
    SealedStateDemo.Conn c = new SealedStateDemo.Closed();
    c = SealedStateDemo.next(c, SealedStateDemo.Event.OPEN);
    c = SealedStateDemo.next(c, SealedStateDemo.Event.SEND);
    assertEquals(new SealedStateDemo.Open(1), c);
  }

  @Test
  void logSessionWritesOnlyWhenOpen(@TempDir Path dir) throws IOException {
    Path f = dir.resolve("s.log");
    LogSession s = new LogSession();
    assertThrows(IllegalStateException.class, () -> s.write("x"));
    s.open(f);
    s.write("a");
    s.close();
    assertEquals(List.of("a"), Files.readAllLines(f));
    assertEquals("CLOSED", s.state());
  }
}
