package oop.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ExTest {
  @Test
  void finallyReturnOverridesTryReturn() {
    assertEquals(2, FinallyReturnDemo.finallyOverrides());
    assertEquals(1, FinallyReturnDemo.finallyCannotChangeLocal());
    assertEquals(3, FinallyReturnDemo.finallySwallowsException());
    assertEquals(java.util.List.of("try", "finally"), FinallyReturnDemo.finallyChangesObject());
  }

  @Test
  void customExceptionCarriesData() {
    var e = assertThrows(InsufficientFundsException.class, () -> {
      CustomExceptionDemo.balance = 10;
      CustomExceptionDemo.withdraw(25);
    });
    assertEquals(15, e.shortBy());
  }

  @Test
  void chainKeepsCause() {
    IOException e = assertThrows(IOException.class, ChainDemo::service);
    assertEquals("connection refused", ChainDemo.root(e).getMessage());
  }

  @Test
  void staticInitFailureThenNoClassDef() {
    assertThrows(ExceptionInInitializerError.class, () -> System.out.println(StaticInitFailureDemo.Config.VALUE));
    assertThrows(NoClassDefFoundError.class, () -> System.out.println(StaticInitFailureDemo.Config.VALUE));
  }

  @Test
  void suppressedAttached() {
    Exception e = assertThrows(RuntimeException.class, () -> {
      try (var a = new TwrSuppressedDemo.R("a")) {
        throw new RuntimeException("body");
      }
    });
    assertEquals(1, e.getSuppressed().length);
    assertInstanceOf(Exception.class, e.getSuppressed()[0]);
  }

  @Test
  void stackOverflowIsAnError() {
    assertTrue(assertThrows(StackOverflowError.class, ErrorsDemoProbe::dive) instanceof Error);
  }

  static final class ErrorsDemoProbe {
    static int dive() {
      return dive() + 1;
    }
  }
}
