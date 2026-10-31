package oop.immut.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 5: fix a leaky class with copies in and out. */
public final class SolutionDefensiveCopy {
  static final class Course {
    private final List<String> students;

    Course(List<String> students) {
      this.students = List.copyOf(students);
    }

    List<String> students() {
      return students;
    }
  }

  public static void main(String[] args) {
    List<String> src = new ArrayList<>(List.of("a"));
    Course c = new Course(src);
    src.add("b");
    System.out.println(c.students());
  }
}
