package oop.coll;

import java.io.IOException;
import java.util.List;

public final class CollCheckedInLambdaBroken {
  static int load(String name) throws IOException {
    return name.length();
  }

  public static void main(String[] args) {
    List<Integer> sizes = List.of("a", "bb").stream().map(n -> load(n)).toList();
    System.out.println(sizes);
  }
}
