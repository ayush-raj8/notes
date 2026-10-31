package oop.overload;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OverloadTest {
  @Test
  void basicsPreferExactSignatures() {
    assertEquals("int:1", OverloadBasics.describe(1));
    assertEquals("long:1", OverloadBasics.describe(1L));
    assertEquals("si:a,1", OverloadBasics.describe("a", 1));
  }

  @Test
  void exactBeatsVarargs() {
    assertEquals("exact-int", ResolutionOrder.pick(1));
    assertEquals("varargs", ResolutionOrder.pick());
  }

  @Test
  void fixedBeatsVarargsPair() {
    assertEquals("fixed", VarargsEdge.f(1, 2));
    assertEquals("varargs len=1", VarargsEdge.f(1));
  }
}
