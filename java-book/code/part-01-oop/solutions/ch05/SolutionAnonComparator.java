package oop.nested.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Exercise 2: anonymous Comparator ordering by length, then alphabetically. */
public final class SolutionAnonComparator {
  public static void main(String[] args) {
    List<String> words = new ArrayList<>(List.of("pear", "fig", "apple", "kiwi"));
    Collections.sort(
        words,
        new Comparator<String>() {
          @Override
          public int compare(String a, String b) {
            int byLen = Integer.compare(a.length(), b.length());
            return byLen != 0 ? byLen : a.compareTo(b);
          }
        });
    System.out.println(words);
  }
}
