package oop.access.solutions;

/** Exercise 4: an override may keep or widen access, never narrow it. */
public final class SolutionToStringPublic {
  @Override
  public String toString() {
    return "ok";
  }

  public static void main(String[] args) {
    System.out.println(new SolutionToStringPublic());
  }
}
