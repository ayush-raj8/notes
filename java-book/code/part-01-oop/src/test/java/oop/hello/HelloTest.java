package oop.hello;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HelloTest {
  @Test
  void greetIncludesName() {
    assertEquals("Hello, Java 21.", new Hello("Java 21").greet());
  }

  @Test
  void edgeFallsBackForBlank() {
    assertEquals("Hello, world.", HelloEdge.greet(""));
    assertEquals("Hello, world.", HelloEdge.greet(null));
  }

  @Test
  void fixedOverloadByParameterList() {
    assertEquals(6, HelloFixed.size("  ab  "));
    assertEquals(2, HelloFixed.size("  ab  ", true));
  }
}
