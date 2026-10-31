package oop.ex;

import java.io.IOException;

public final class ExOverrideNewCheckedBroken {
  static class Parent {
    void run() {}
  }

  static class Child extends Parent {
    @Override
    void run() throws IOException {}
  }
}
