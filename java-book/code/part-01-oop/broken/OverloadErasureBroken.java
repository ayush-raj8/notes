package oop.overload;

import java.util.List;

/** Generics erasure clash: List<String> and List<Integer> erase to List. */
public final class OverloadErasureBroken {
  public static void take(List<String> list) {}

  public static void take(List<Integer> list) {}
}
