package oop.coll;

import java.util.ArrayList;
import java.util.List;

public final class CollListCovarianceBroken {
  public static void main(String[] args) {
    List<Object> objects = new ArrayList<String>();
    objects.add(1);
  }
}
