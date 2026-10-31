package patterns.creational.singleton.exam;

import patterns.creational.singleton.CloneableSingleton;

// ANCHOR: predict-clone-singleton
public final class PredictCloneSingleton {
  public static void main(String[] args) {
    CloneableSingleton a = CloneableSingleton.getInstance();
    CloneableSingleton b = a.clone();
    System.out.println(a == b);
    System.out.println(a == CloneableSingleton.getInstance());
    System.out.println(a.getClass() == b.getClass());
  }
}
// ANCHOR_END: predict-clone-singleton
