package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import patterns.behavioral.iterator.IntTree;
import patterns.behavioral.iterator.LineIterable;
import patterns.behavioral.iterator.Range;

class IteratorTest {
  @Test
  void rangeStepsAndStops() {
    List<Integer> out = new ArrayList<>();
    for (int x : new Range(0, 10, 4)) out.add(x);
    assertEquals(List.of(0, 4, 8), out);
  }

  @Test
  void eachIteratorIsIndependent() {
    Range r = new Range(0, 2, 1);
    Iterator<Integer> a = r.iterator();
    Iterator<Integer> b = r.iterator();
    a.next();
    assertEquals(0, b.next());
  }

  @Test
  void nextPastEndThrows() {
    Iterator<Integer> it = new Range(0, 1, 1).iterator();
    it.next();
    assertFalse(it.hasNext());
    assertThrows(NoSuchElementException.class, it::next);
  }

  @Test
  void treeIsInOrderWithoutDuplicates() {
    IntTree t = new IntTree();
    for (int v : new int[] {5, 2, 8, 2, 1}) t.add(v);
    List<Integer> out = new ArrayList<>();
    t.forEach(out::add);
    assertEquals(List.of(1, 2, 5, 8), out);
  }

  @Test
  void lineIteratorClosesAtEnd(@TempDir Path dir) throws IOException {
    Path f = dir.resolve("a.txt");
    Files.writeString(f, "x\ny");
    LineIterable.LineIterator it = new LineIterable(f).iterator();
    assertEquals("x", it.next());
    assertFalse(it.isClosed());
    assertEquals("y", it.next());
    assertTrue(it.isClosed());
  }
}
