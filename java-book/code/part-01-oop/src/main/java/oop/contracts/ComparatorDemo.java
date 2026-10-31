package oop.contracts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// ANCHOR: comparator-demo
public final class ComparatorDemo {
  record Emp(String dept, String name, int pay) {}

  public static void main(String[] args) {
    List<Emp> list = new ArrayList<>(List.of(
        new Emp("ops", "Zed", 95),
        new Emp("dev", "Amy", 90),
        new Emp("dev", "Bob", 90),
        new Emp("ops", "Eve", 70)));

    list.sort(Comparator.comparing(Emp::dept).thenComparing(Emp::name));
    System.out.println(list.stream().map(Emp::name).toList());

    list.sort(Comparator.comparingInt(Emp::pay).reversed().thenComparing(Emp::name));
    System.out.println(list.stream().map(Emp::name).toList());

    List<String> words = new ArrayList<>(java.util.Arrays.asList("pear", null, "fig", "apple"));
    words.sort(Comparator.nullsFirst(Comparator.comparing(String::length)));
    System.out.println(words);

    List<String> mixed = new ArrayList<>(List.of("b", "A", "c", "D"));
    mixed.sort(null);
    System.out.println(mixed);
    mixed.sort(String.CASE_INSENSITIVE_ORDER);
    System.out.println(mixed);
    mixed.sort(Comparator.reverseOrder());
    System.out.println(mixed);
  }
}
// ANCHOR_END: comparator-demo
