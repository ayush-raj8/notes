package oop.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class ContractsTest {
  @Test
  void comparableSortsByAge() {
    List<ComparableBasics.Person> list = new ArrayList<>();
    list.add(new ComparableBasics.Person("Cy", 40));
    list.add(new ComparableBasics.Person("Ann", 25));
    list.sort(null);
    assertEquals("Ann", list.get(0).name);
  }

  @Test
  void subtractionComparatorOverflows() {
    Comparator<Integer> bad = (a, b) -> a - b;
    assertTrue(bad.compare(Integer.MAX_VALUE, -2) < 0);
    assertTrue(Integer.compare(Integer.MAX_VALUE, -2) > 0);
  }

  @Test
  void compareToInconsistentWithEquals() {
    BigDecimal a = new BigDecimal("1.0");
    BigDecimal b = new BigDecimal("1.00");
    Set<BigDecimal> hash = new HashSet<>(List.of(a, b));
    Set<BigDecimal> tree = new TreeSet<>(List.of(a, b));
    assertEquals(2, hash.size());
    assertEquals(1, tree.size());
  }

  @Test
  void rangeIsIterableAndExhausts() {
    var it = new IterableDemo.Range(0, 1).iterator();
    assertTrue(it.hasNext());
    assertEquals(0, it.next());
    assertFalse(it.hasNext());
    assertThrows(NoSuchElementException.class, it::next);
  }

  @Test
  void treeSetRejectsNonComparable() {
    Set<Object> s = new TreeSet<>();
    assertThrows(ClassCastException.class, () -> s.add(new Object()));
  }
}
