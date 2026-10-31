package oop.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExExtrasTest {
  @Test
  void failedConstructorLeavesNoObjectButLeaksHalfBuiltThis() {
    ExChild0 c = null;
    try {
      c = new ExChild0(true);
    } catch (IllegalStateException expected) {
      // fall through
    }
    assertNull(c);

    assertThrows(IllegalStateException.class, () -> new ExLeaky(true));
    assertNotNull(ExLeaky.last);
    assertEquals(0, ExLeaky.last.value);
  }

  @Test
  void resourceThatFailsToOpenClosesEarlierResources() {
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
      try (ExRes a = ExTwrOrder.open("A", false);
          ExRes b = ExTwrOrder.open("B", true)) {
        throw new AssertionError("body must not run");
      }
    });
    assertEquals("cannot open B", e.getMessage());
  }

  @Test
  void closeFailureIsSuppressedWhenBodyThrows() {
    RuntimeException e = assertThrows(RuntimeException.class, () -> {
      try (ExRes a = new ExRes("A", true)) {
        throw new RuntimeException("body");
      }
    });
    assertEquals("body", e.getMessage());
    assertEquals(1, e.getSuppressed().length);
    assertEquals("close-A", e.getSuppressed()[0].getMessage());
  }
}
