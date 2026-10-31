package oop.immut.exam;

// ANCHOR: predict-enum-singleton
public final class PredictEnumSingleton {
  enum Config {
    INSTANCE;

    int hits;
  }

  public static void main(String[] args) {
    Config.INSTANCE.hits++;
    Config c = Config.valueOf("INSTANCE");
    c.hits++;
    System.out.println(Config.INSTANCE.hits + " " + (c == Config.INSTANCE));
  }
}
// ANCHOR_END: predict-enum-singleton
