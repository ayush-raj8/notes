package patterns.behavioral.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class StrategyTest {
  @Test
  void strategiesCompute() {
    assertEquals(1500, new Cart(new NoDiscount()).add(1000).add(500).total());
    assertEquals(1200, new Cart(new PercentOff(20)).add(1500).total());
    assertEquals(0, new Cart(new FlatOff(5000)).add(100).total());
  }

  @Test
  void swapAtRuntime() {
    Cart c = new Cart(new NoDiscount()).add(1000);
    c.setStrategy(base -> base / 2);
    assertEquals(500, c.total());
  }

  @Test
  void rejectsBadInput() {
    assertThrows(NullPointerException.class, () -> new Cart(null));
    assertThrows(IllegalArgumentException.class, () -> new PercentOff(101));
  }

  @Test
  void enumStrategies() {
    assertEquals(7, Op.ADD.apply(3, 4));
    assertEquals(12, Op.MUL.apply(3, 4));
    assertEquals(4, Op.MAX.apply(3, 4));
  }

  @Test
  void comparatorIsAStrategy() {
    List<String> w = new ArrayList<>(List.of("bb", "a", "ccc"));
    w.sort(Comparator.comparingInt(String::length).reversed());
    assertEquals(List.of("ccc", "bb", "a"), w);
  }
}
