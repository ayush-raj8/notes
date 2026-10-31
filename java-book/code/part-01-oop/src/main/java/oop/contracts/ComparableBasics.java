package oop.contracts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: comparable-basics
public final class ComparableBasics {
  static final class Person implements Comparable<Person> {
    final String name;
    final int age;

    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }

    @Override
    public int compareTo(Person other) {
      return Integer.compare(age, other.age);
    }

    @Override
    public String toString() {
      return name + ":" + age;
    }
  }

  public static void main(String[] args) {
    List<Person> people = new ArrayList<>();
    people.add(new Person("Cy", 40));
    people.add(new Person("Ann", 25));
    people.add(new Person("Bo", 31));
    Collections.sort(people);
    System.out.println(people);
    System.out.println(people.get(0).compareTo(people.get(1)));
    System.out.println(people.get(2).compareTo(people.get(1)));
    System.out.println(people.get(1).compareTo(people.get(1)));
  }
}
// ANCHOR_END: comparable-basics
