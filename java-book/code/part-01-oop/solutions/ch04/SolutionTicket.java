package oop.access.solutions;

/** Exercise 1: static counter, final id, private state, public accessors. */
public final class SolutionTicket {
  private static int next = 1;
  private final int id;

  public SolutionTicket() {
    id = next++;
  }

  public int id() {
    return id;
  }

  public static int issued() {
    return next - 1;
  }

  public static void main(String[] args) {
    SolutionTicket a = new SolutionTicket();
    SolutionTicket b = new SolutionTicket();
    System.out.println(a.id() + " " + b.id() + " " + SolutionTicket.issued());
  }
}
