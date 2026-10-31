package oop.uml.solutions;

import java.util.List;

/** Diagram -> code: Payable <|.. Employee <|-- Manager, Intern. Employee is abstract. */
public final class SolutionEmployee {
  interface Payable {
    int pay();
  }

  abstract static class Employee implements Payable {
    private final String name;

    Employee(String name) {
      this.name = name;
    }

    String name() {
      return name;
    }

    @Override
    public abstract int pay();
  }

  static final class Manager extends Employee {
    private final int bonus;

    Manager(String name, int bonus) {
      super(name);
      this.bonus = bonus;
    }

    @Override
    public int pay() {
      return 5000 + bonus;
    }
  }

  static final class Intern extends Employee {
    Intern(String name) {
      super(name);
    }

    @Override
    public int pay() {
      return 1000;
    }
  }

  public static void main(String[] args) {
    List<Employee> staff = List.of(new Manager("Mia", 500), new Intern("Ivo"));
    for (Employee e : staff) System.out.println(e.name() + " " + e.pay());
    int total = staff.stream().mapToInt(Payable::pay).sum();
    System.out.println("total " + total);
  }
}
