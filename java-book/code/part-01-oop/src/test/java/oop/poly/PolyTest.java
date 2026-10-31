package oop.poly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PolyTest {
  @Test
  void overloadUsesStaticTypeOverrideUsesObject() {
    PzAnimal a = new PzDog();
    assertEquals("animal", PolyCompileVsRuntime.describe(a));
    assertEquals("dog", PolyCompileVsRuntime.describe((PzDog) a));
    assertEquals("woof", a.sound());
  }

  @Test
  void badDowncastThrows() {
    PzAnimal a = new PzDog();
    assertThrows(ClassCastException.class, () -> {
      PzCat c = (PzCat) a;
      c.sound();
    });
  }

  @Test
  void castingNullIsFine() {
    PzAnimal none = null;
    PzDog d = (PzDog) none;
    assertEquals(null, d);
    assertFalse(none instanceof PzDog);
  }

  @Test
  void patternMatching() {
    assertEquals("long abcd", PolyInstanceofPattern.modern("abcd"));
    assertEquals("not int", PolyInstanceofPattern.modern("ab"));
    assertEquals("int 6", PolyInstanceofPattern.modern(5));
    assertEquals("big 42", PolyInstanceofPattern.sw(42));
    assertEquals("null", PolyInstanceofPattern.sw(null));
  }

  @Test
  void arraysAreCovariantAndChecked() {
    Object[] objs = new String[1];
    assertThrows(ArrayStoreException.class, () -> objs[0] = 1);
  }

  @Test
  void interfaceAndClassDispatch() {
    PzShape sq = new PzSquare();
    assertEquals(4.0, PolyDispatchBytecode.viaClass(sq));
    assertEquals("sq:shape", PolyDispatchBytecode.viaInterface(sq));
  }
}
