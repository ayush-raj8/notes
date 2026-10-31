package patterns.creational.singleton.exam;

import patterns.creational.singleton.EnumSingleton;

// ANCHOR: predict-enum-counter
public final class PredictEnumCounter {
  public static void main(String[] args) {
    EnumSingleton a = EnumSingleton.INSTANCE;
    EnumSingleton b = EnumSingleton.valueOf("INSTANCE");
    System.out.println(a == b);
    System.out.println(a.next());
    System.out.println(b.next());
    System.out.println(a.name() + " " + a.ordinal());
  }
}
// ANCHOR_END: predict-enum-counter
