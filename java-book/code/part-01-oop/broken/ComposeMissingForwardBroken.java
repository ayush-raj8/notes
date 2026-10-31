package oop.compose;

/** Intentionally invalid: wrapper implements the interface but forgets one method. */
public final class ComposeMissingForwardBroken {
  interface Store {
    void put(String s);

    int size();
  }

  static final class Wrapper implements Store {
    private final Store inner;

    Wrapper(Store inner) {
      this.inner = inner;
    }

    public void put(String s) {
      inner.put(s);
    }
  }
}
