package oop.objectc;

import java.util.Arrays;

// ANCHOR: clone-demo
/** {@code super.clone()} is shallow: reference fields are shared. */
public final class CloneDemo {
  static final class Team implements Cloneable {
    String name;
    int[] scores;

    Team(String name, int[] scores) {
      this.name = name;
      this.scores = scores;
    }

    @Override
    public Team clone() {
      try {
        return (Team) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new AssertionError(e);
      }
    }

    Team deepClone() {
      Team copy = clone();
      copy.scores = scores.clone();
      return copy;
    }
  }

  public static void main(String[] args) {
    Team a = new Team("A", new int[] {1, 2});
    Team shallow = a.clone();
    Team deep = a.deepClone();
    a.scores[0] = 99;
    a.name = "changed";
    System.out.println("original: " + a.name + " " + Arrays.toString(a.scores));
    System.out.println("shallow : " + shallow.name + " " + Arrays.toString(shallow.scores));
    System.out.println("deep    : " + deep.name + " " + Arrays.toString(deep.scores));
    System.out.println("shared array: " + (a.scores == shallow.scores));
  }
}
// ANCHOR_END: clone-demo
