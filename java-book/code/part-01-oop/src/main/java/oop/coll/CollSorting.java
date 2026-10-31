package oop.coll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// ANCHOR: coll-sorting
record CollStudent(String name, int marks) {}

public final class CollSorting {
  public static void main(String[] args) {
    List<String> words = new ArrayList<>(List.of("pear", "Apple", "fig", "apple"));
    Collections.sort(words); // natural order (Comparable): uppercase before lowercase
    System.out.println("natural        : " + words);
    words.sort(String.CASE_INSENSITIVE_ORDER);
    System.out.println("ignore case    : " + words + "  (sort is stable: Apple stays before apple)");
    words.sort(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()));
    System.out.println("length, natural: " + words);
    words.sort(Comparator.comparing(String::length).reversed());
    System.out.println("length desc    : " + words);

    List<CollStudent> st = new ArrayList<>(List.of(
        new CollStudent("Ravi", 80), new CollStudent("Asha", 92),
        new CollStudent("Meena", 80), new CollStudent("Dev", 75)));
    st.sort(Comparator.comparingInt(CollStudent::marks).reversed().thenComparing(CollStudent::name));
    System.out.println("marks desc, name:");
    st.forEach(s -> System.out.println("  " + s.name() + " " + s.marks()));

    int[] prim = {5, 3, 9, 1};
    Arrays.sort(prim);
    System.out.println("Arrays.sort(int[]): " + Arrays.toString(prim));
    Integer[] boxed = {5, 3, 9, 1};
    Arrays.sort(boxed, Collections.reverseOrder());
    System.out.println("Arrays.sort(Integer[], reverseOrder): " + Arrays.toString(boxed));

    List<Integer> nums = new ArrayList<>(List.of(3, 1, 2));
    Collections.reverse(nums);
    System.out.println("reverse: " + nums + "  max=" + Collections.max(nums) + " min=" + Collections.min(nums)
        + " frequency(1)=" + Collections.frequency(nums, 1));
  }
}
// ANCHOR_END: coll-sorting
