package oop.coll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class CollTest {
  @Test
  void removeIndexVersusObject() {
    List<Integer> l = new ArrayList<>(List.of(1, 2, 3));
    l.remove(1);
    assertEquals(List.of(1, 3), l);
    l.remove(Integer.valueOf(1));
    assertEquals(List.of(3), l);
  }

  @Test
  void asListIsFixedSizeAndWriteThrough() {
    Integer[] arr = {1, 2, 3};
    List<Integer> view = Arrays.asList(arr);
    arr[0] = 9;
    assertEquals(9, view.get(0));
    assertThrows(UnsupportedOperationException.class, () -> view.add(4));
    assertEquals(1, Arrays.asList(new int[] {1, 2, 3}).size());
  }

  @Test
  void listOfRejectsNullAndMutation() {
    assertThrows(NullPointerException.class, () -> List.of(1, null));
    assertThrows(UnsupportedOperationException.class, () -> List.of(1).add(2));
  }

  @Test
  void forEachRemoveOfSecondToLastDoesNotThrow() {
    List<String> a = new ArrayList<>(List.of("a", "b", "c"));
    assertThrows(java.util.ConcurrentModificationException.class, () -> {
      for (String s : a) {
        if (s.equals("a")) {
          a.remove(s);
        }
      }
    });
    List<String> b = new ArrayList<>(List.of("a", "b", "c"));
    for (String s : b) {
      if (s.equals("b")) {
        b.remove(s);
      }
    }
    assertEquals(List.of("a", "c"), b);
  }

  @Test
  void treeSetUsesCompareNotEquals() {
    List<BigDecimal> v = List.of(new BigDecimal("1.0"), new BigDecimal("1.00"));
    assertEquals(2, new HashSet<>(v).size());
    assertEquals(1, new TreeSet<>(v).size());
  }

  @Test
  void priorityQueueToStringIsHeapOrderPollIsSorted() {
    PriorityQueue<Integer> pq = new PriorityQueue<>();
    for (int x : new int[] {5, 1, 4, 2, 3}) {
      pq.add(x);
    }
    assertEquals("[1, 2, 4, 5, 3]", pq.toString());
    assertEquals(1, pq.poll());
    assertEquals(2, pq.poll());
  }

  @Test
  void mutatedKeyIsLost() {
    Map<CollKey, String> m = new HashMap<>();
    CollKey k = new CollKey(1);
    m.put(k, "v");
    k.id = 2;
    assertNull(m.get(k));
    assertEquals(1, m.size());
  }

  @Test
  void shortSetRemoveNeverMatches() {
    Set<Short> s = new HashSet<>();
    for (short i = 0; i < 5; i++) {
      s.add(i);
      s.remove(i - 1);
    }
    assertEquals(5, s.size());
  }

  @Test
  void mergeCountsWords() {
    Map<String, Integer> m = new HashMap<>();
    for (String w : "a b a".split(" ")) {
      m.merge(w, 1, Integer::sum);
    }
    assertEquals(2, m.get("a"));
  }

  @Test
  void streamIsLazyAndSingleUse() {
    int[] calls = {0};
    Stream<Integer> s = Stream.of(1, 2, 3).peek(n -> calls[0]++);
    assertEquals(0, calls[0]);
    s.forEach(n -> { });
    assertEquals(3, calls[0]);
  }

  @Test
  void streamReuseThrows() {
    Stream<Integer> s = Stream.of(1, 2);
    s.count();
    assertThrows(IllegalStateException.class, s::count);
  }

  @Test
  void toMapDuplicateThrows() {
    IllegalStateException e = assertThrows(IllegalStateException.class,
        () -> Stream.of("a", "a").collect(Collectors.toMap(w -> w, w -> 1)));
    assertTrue(e.getMessage().startsWith("Duplicate key a"));
  }

  @Test
  void orElseEvaluatesArgumentEagerly() {
    int[] calls = {0};
    Optional.of("x").orElse(make(calls));
    assertEquals(1, calls[0]);
    Optional.of("x").orElseGet(() -> make(calls));
    assertEquals(1, calls[0]);
  }

  private static String make(int[] calls) {
    calls[0]++;
    return "m";
  }

  @Test
  void lruEvictsLeastRecentlyUsed() {
    CollLruCache<String, Integer> c = new CollLruCache<>(2);
    c.put("a", 1);
    c.put("b", 2);
    c.get("a");
    c.put("c", 3);
    assertEquals(List.of("a", "c"), List.copyOf(c.keySet()));
    assertFalse(c.containsKey("b"));
  }
}
