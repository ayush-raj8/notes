package oop.contracts.solutions;

/** AutoCloseable that prints enter/exit; close() declares no checked exception. */
public final class SolutionTimer implements AutoCloseable {
  private final String label;

  public SolutionTimer(String label) {
    this.label = label;
    System.out.println("enter " + label);
  }

  @Override
  public void close() {
    System.out.println("exit " + label);
  }

  public static void main(String[] args) {
    try (SolutionTimer outer = new SolutionTimer("outer"); SolutionTimer inner = new SolutionTimer("inner")) {
      System.out.println("work");
    }
  }
}
