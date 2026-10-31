package oop.generics;

import java.util.ArrayList;
import java.util.List;

/** Intentionally invalid: a ? extends list is read-only (except null). */
public final class GenWildcardAddBroken {
  public static void main(String[] args) {
    List<? extends Number> xs = new ArrayList<Integer>();
    xs.add(1);
  }
}
