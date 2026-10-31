package patterns.behavioral.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// ANCHOR: jdk-strategy-demo
/** Comparator is the JDK's strategy: the sort algorithm stays, the ordering is passed in. */
public final class JdkStrategyDemo {
  public static void main(String[] args) {
    List<String> words = new ArrayList<>(List.of("pear", "fig", "banana", "kiwi"));
    Collections.sort(words);
    System.out.println(words);
    words.sort(Comparator.comparingInt(String::length));
    System.out.println(words);
    words.sort(Comparator.comparingInt(String::length).reversed().thenComparing(Comparator.naturalOrder()));
    System.out.println(words);
    String[] arr = {"b", "C", "a"};
    Arrays.sort(arr, String.CASE_INSENSITIVE_ORDER);
    System.out.println(Arrays.toString(arr));
  }
}
// ANCHOR_END: jdk-strategy-demo
