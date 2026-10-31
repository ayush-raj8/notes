package oop.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class MemoryTest {
  @Test
  void distinctNewsAreDifferentObjects() {
    Point a = new Point(1, 2);
    Point b = new Point(1, 2);
    assertNotSame(a, b);
    assertEquals("(1, 2)", a.toString());
  }

  @Test
  void aliasSharesMutation() {
    Point a = new Point(0, 0);
    Point b = a;
    b.moveBy(2, 3);
    assertSame(a, b);
    assertEquals("(2, 3)", a.toString());
  }

  @Test
  void rebindParameterDoesNotRebindCaller() {
    Point origin = new Point(0, 0);
    PassByValueDemo.rebind(origin);
    assertEquals("(0, 0)", origin.toString());
  }

  @Test
  void mutateParameterMutatesSharedObject() {
    Point origin = new Point(0, 0);
    PassByValueDemo.mutate(origin);
    assertEquals("(10, 0)", origin.toString());
  }

  @Test
  void thisChainingUpdatesValue() {
    assertEquals(6, new ThisDemo(1).bump(2).bump(3).value());
  }

  @Test
  void varLocalsSize() {
    assertEquals(2, VarLocals.localSize());
  }

  @Test
  void shadowWithoutThisLeavesFieldZero() {
    ThisShadowGotcha g = new ThisShadowGotcha();
    g.setWrong(7);
    assertEquals(0, g.value());
    g.setRight(7);
    assertEquals(7, g.value());
  }
}
