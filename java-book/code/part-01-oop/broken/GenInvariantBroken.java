package oop.generics;

import java.util.ArrayList;
import java.util.List;

/** Intentionally invalid: List<String> is not a List<Object>. */
public final class GenInvariantBroken {
  public static void main(String[] args) {
    List<String> strings = new ArrayList<>();
    List<Object> objects = strings;
  }
}
