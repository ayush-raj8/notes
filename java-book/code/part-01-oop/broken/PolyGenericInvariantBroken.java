package oop.poly;

import java.util.ArrayList;
import java.util.List;

/** Generics are invariant: List<String> is not a List<Object>. */
public class PolyGenericInvariantBroken {
  public static void main(String[] args) {
    List<Object> objs = new ArrayList<String>();
    objs.add(1);
  }
}
