package oop.coll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CollSortNonComparableBroken {
  static class Plain {
    int n;
  }

  public static void main(String[] args) {
    List<Plain> items = new ArrayList<>();
    Collections.sort(items);
  }
}
