package oop.contracts.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Natural order by id (Comparable) plus a Comparator by name then id. */
public final class SolutionSortStudents {
  record Student(int id, String name) implements Comparable<Student> {
    @Override
    public int compareTo(Student o) {
      return Integer.compare(id, o.id);
    }
  }

  public static void main(String[] args) {
    List<Student> list = new ArrayList<>(List.of(new Student(3, "Ann"), new Student(1, "Bob"), new Student(2, "Ann")));
    Collections.sort(list);
    System.out.println(list);
    list.sort(Comparator.comparing(Student::name).thenComparing(Student::id));
    System.out.println(list);
  }
}
