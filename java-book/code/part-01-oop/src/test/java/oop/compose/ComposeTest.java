package oop.compose;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class ComposeTest {
  @Test
  void inheritanceDoubleCounts() {
    CountingHashSet<String> s = new CountingHashSet<>();
    s.addAll(List.of("a", "b", "c"));
    assertEquals(6, s.addCount());
    assertEquals(3, s.size());
  }

  @Test
  void delegationCountsOnce() {
    CountingSet<String> s = new CountingSet<>(new HashSet<>());
    s.addAll(List.of("a", "b", "c"));
    assertEquals(3, s.addCount());
    assertEquals(3, s.size());
  }

  @Test
  void fragileVersionsDiffer() {
    FragileVersions.CountingV1 a = new FragileVersions.CountingV1();
    a.addAll(List.of("x", "y", "z"));
    FragileVersions.CountingV2 b = new FragileVersions.CountingV2();
    b.addAll(List.of("x", "y", "z"));
    assertEquals(6, a.count);
    assertEquals(3, b.count);
  }
}
