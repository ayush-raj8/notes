package oop.access.solutions;

/** Exercise 5: a blank final is assigned exactly once on every constructor path. */
public final class SolutionBlankFinalChain {
  private final int limit;

  public SolutionBlankFinalChain() {
    this(10);
  }

  public SolutionBlankFinalChain(int limit) {
    this.limit = limit;
  }

  public int limit() {
    return limit;
  }

  public static void main(String[] args) {
    System.out.println(new SolutionBlankFinalChain().limit());
    System.out.println(new SolutionBlankFinalChain(4).limit());
  }
}
