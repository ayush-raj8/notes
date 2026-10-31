package oop.compare.solutions;

/** Exercise 5: shadowing parameter; fix the broken setter with this. */
public final class SolutionShadowSetter {
  private int level;

  void brokenSet(int level) {
    level = level; // assigns the parameter to itself
  }

  void set(int level) {
    this.level = level;
  }

  public static void main(String[] args) {
    SolutionShadowSetter s = new SolutionShadowSetter();
    s.brokenSet(5);
    System.out.println(s.level);
    s.set(5);
    System.out.println(s.level);
  }
}
