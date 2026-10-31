package oop.init.solutions;

/** Exercise 1: count objects with an instance initializer shared by two constructors. */
public final class SolutionInstanceCounter {
  private static int created;
  private final int serial;

  {
    created++;
  }

  public SolutionInstanceCounter() {
    this(0);
  }

  public SolutionInstanceCounter(int ignored) {
    serial = created;
  }

  public static void main(String[] args) {
    new SolutionInstanceCounter();
    new SolutionInstanceCounter(9);
    SolutionInstanceCounter last = new SolutionInstanceCounter();
    System.out.println("created=" + created + ", last serial=" + last.serial);
  }
}
