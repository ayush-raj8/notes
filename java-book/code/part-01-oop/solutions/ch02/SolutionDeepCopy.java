package oop.ctors.solutions;

import java.util.Arrays;

/** Exercise: deep copy constructor for an int[]. */
public final class SolutionDeepCopy {
  private final int[] history;

  public SolutionDeepCopy(int[] history) {
    this.history = Arrays.copyOf(history, history.length);
  }

  public SolutionDeepCopy(SolutionDeepCopy other) {
    this.history = Arrays.copyOf(other.history, other.history.length);
  }

  public void set(int i, int v) {
    history[i] = v;
  }

  public int get(int i) {
    return history[i];
  }

  public static void main(String[] args) {
    SolutionDeepCopy a = new SolutionDeepCopy(new int[] {1, 2});
    SolutionDeepCopy b = new SolutionDeepCopy(a);
    b.set(0, 99);
    System.out.println(a.get(0) + " " + b.get(0));
  }
}
