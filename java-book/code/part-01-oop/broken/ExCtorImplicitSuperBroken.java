package oop.ex;

import java.io.IOException;

public final class ExCtorImplicitSuperBroken {
  static class Base {
    Base() throws IOException {}
  }

  static class Derived extends Base {
    Derived() {}
  }
}
