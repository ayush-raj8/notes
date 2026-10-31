package oop.ex;

public class ExStaticInitCheckedBroken {
  static final int LIMIT;

  static {
    if (System.currentTimeMillis() < 0) {
      throw new Exception("bad config");
    }
    LIMIT = 1;
  }
}
