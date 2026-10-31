package oop.coll;

import java.util.List;

public final class CollStreamToListBroken {
  public static void main(String[] args) {
    List<String> names = List.of("a", "bb");
    List<Integer> lengths = names.stream().map(String::length);
    System.out.println(lengths);
  }
}
