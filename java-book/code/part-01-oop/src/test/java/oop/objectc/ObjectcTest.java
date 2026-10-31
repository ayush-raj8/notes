package oop.objectc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ObjectcTest {
  @Test
  void goodPointHonoursContract() {
    GoodPoint p = new GoodPoint(1, 2);
    GoodPoint q = new GoodPoint(1, 2);
    assertEquals(p, q);
    assertEquals(p.hashCode(), q.hashCode());
    assertEquals("GoodPoint[x=1, y=2]", p.toString());
    Set<GoodPoint> s = new HashSet<>();
    s.add(p);
    assertTrue(s.contains(q));
    assertFalse(p.equals(null));
  }

  @Test
  void shallowCloneSharesArray() {
    CloneDemo.Team a = new CloneDemo.Team("A", new int[] {1, 2});
    CloneDemo.Team shallow = a.clone();
    CloneDemo.Team deep = a.deepClone();
    assertNotSame(a, shallow);
    assertTrue(a.scores == shallow.scores);
    assertFalse(a.scores == deep.scores);
  }

  @Test
  void symmetryBreaksWithInstanceof() {
    SymmetryDemo.Pt p = new SymmetryDemo.Pt(1);
    SymmetryDemo.ColorPt c = new SymmetryDemo.ColorPt(1, "red");
    assertTrue(p.equals(c));
    assertFalse(c.equals(p));
  }

  @Test
  void mutableKeyIsLost() {
    Set<MutableKeyGotcha.Key> set = new HashSet<>();
    MutableKeyGotcha.Key k = new MutableKeyGotcha.Key(1);
    set.add(k);
    k.id = 2;
    assertFalse(set.contains(k));
    assertEquals(1, set.size());
  }
}
