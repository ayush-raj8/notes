package oop.ex;

import java.io.IOException;

public final class ExDefaultCtorBroken {
  static class Base {
    Base() throws IOException {}
  }

  static class Derived extends Base {}
}
