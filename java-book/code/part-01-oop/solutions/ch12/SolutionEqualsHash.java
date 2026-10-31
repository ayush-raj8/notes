package oop.objectc.solutions;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** Exercise 1: Person equals/hashCode on name + age. */
public final class SolutionEqualsHash {
  static final class Person {
    private final String name;
    private final int age;

    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Person p && age == p.age && Objects.equals(name, p.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, age);
    }
  }

  public static void main(String[] args) {
    Set<Person> s = new HashSet<>();
    s.add(new Person("Ada", 36));
    System.out.println(s.contains(new Person("Ada", 36)));
  }
}
