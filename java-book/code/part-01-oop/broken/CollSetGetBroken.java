package oop.coll;

import java.util.HashSet;
import java.util.Set;

public final class CollSetGetBroken {
  public static void main(String[] args) {
    Set<String> names = new HashSet<>();
    names.add("a");
    String first = names.get(0);
  }
}
