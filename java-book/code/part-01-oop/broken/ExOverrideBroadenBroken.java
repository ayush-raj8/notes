package oop.ex;

import java.io.IOException;

public final class ExOverrideBroadenBroken {
  static class Parent {
    void read() throws IOException {}
  }

  static class Child extends Parent {
    @Override
    void read() throws Exception {}
  }
}
