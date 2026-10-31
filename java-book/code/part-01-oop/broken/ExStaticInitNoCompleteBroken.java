package oop.ex;

public class ExStaticInitNoCompleteBroken {
  static {
    throw new IllegalStateException("bad config");
  }
}
