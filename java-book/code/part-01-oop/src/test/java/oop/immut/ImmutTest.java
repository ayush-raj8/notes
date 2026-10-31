package oop.immut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ImmutTest {
  @Test
  void rosterIsolatesCallerList() {
    List<String> src = new ArrayList<>(List.of("a"));
    ImmutableRoster r = new ImmutableRoster(src);
    src.add("b");
    assertEquals(List.of("a"), r.names());
    assertThrows(UnsupportedOperationException.class, () -> r.names().add("x"));
    assertEquals(List.of("a", "c"), r.with("c").names());
    assertEquals(List.of("a"), r.names());
  }

  @Test
  void leakyRosterLeaks() {
    List<String> src = new ArrayList<>(List.of("a"));
    LeakyRoster r = new LeakyRoster(src);
    src.add("b");
    assertEquals(2, r.names().size());
  }

  @Test
  void recordCompactValidatesAndNormalises() {
    assertThrows(IllegalArgumentException.class, () -> new RecordCompact.Range(2, 1));
    assertEquals("x", new RecordCompact.Name("  x ").value());
  }

  @Test
  void recordArrayComponentUsesReferenceEquality() {
    RecordArrayGotcha.Data a = new RecordArrayGotcha.Data(new int[] {1});
    RecordArrayGotcha.Data b = new RecordArrayGotcha.Data(new int[] {1});
    assertFalse(a.equals(b));
    assertTrue(new RecordArrayGotcha.SafeData(new int[] {1}).equals(new RecordArrayGotcha.SafeData(new int[] {1})));
  }

  @Test
  void enumFacts() {
    assertSame(EnumBasics.Level.HIGH, EnumBasics.Level.valueOf("HIGH"));
    assertEquals(10, EnumBasics.Level.HIGH.weight());
    assertEquals(42, EnumAbstract.Op.TIMES.apply(6, 7));
    assertThrows(IllegalArgumentException.class, () -> EnumBasics.Level.valueOf("NOPE"));
  }

  @Test
  void enumSingletonCounts() {
    int first = EnumSingleton.Registry.INSTANCE.nextId();
    assertEquals(first + 1, EnumSingleton.Registry.INSTANCE.nextId());
  }
}
