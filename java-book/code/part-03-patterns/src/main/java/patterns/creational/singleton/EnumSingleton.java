package patterns.creational.singleton;

// ANCHOR: enum-singleton
public enum EnumSingleton {
  INSTANCE;

  private int counter;

  public int next() {
    return ++counter;
  }
}
// ANCHOR_END: enum-singleton
