package oop.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GenericsTest {
  @Test
  void boundedMax() {
    assertEquals(9, BoundedDemo.max(3, 9));
    assertEquals("pear", BoundedDemo.max("pear", "apple"));
  }

  @Test
  void pecsCopy() {
    List<Number> dst = new ArrayList<>();
    PecsDemo.copy(dst, List.of(1, 2));
    assertEquals(List.of(1, 2), dst);
    assertEquals(6.0, PecsDemo.sum(List.of(1, 2.5, 2.5)));
  }

  @Test
  void erasureSameClass() {
    assertTrue(new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass());
  }

  @Test
  void arraysAreCovariantAtRuntime() {
    Object[] arr = new String[1];
    assertThrows(ArrayStoreException.class, () -> arr[0] = 1);
  }

  @Test
  void staticSharedAcrossTypeArgs() {
    int before = StaticSharedGotcha.Box.created;
    new StaticSharedGotcha.Box<String>();
    new StaticSharedGotcha.Box<Integer>();
    assertEquals(before + 2, StaticSharedGotcha.Box.created);
  }

  @Test
  void captureSwap() {
    List<String> xs = new ArrayList<>(List.of("a", "b"));
    WildcardCapture.swapFirstTwo(xs);
    assertEquals(List.of("b", "a"), xs);
  }
}
