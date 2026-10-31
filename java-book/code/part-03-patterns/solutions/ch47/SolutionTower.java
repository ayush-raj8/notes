package solutions.ch47;

import java.util.ArrayDeque;
import java.util.Queue;

/** Exercise 1: planes never talk to each other; the tower grants one runway at a time. */
public final class SolutionTower {
  static final class Tower {
    private final Queue<Plane> waiting = new ArrayDeque<>();
    private Plane onRunway;

    void requestLanding(Plane p) {
      if (onRunway == null) {
        onRunway = p;
        p.log("landing");
      } else {
        waiting.add(p);
        p.log("hold (" + onRunway.name + " on runway)");
      }
    }

    void landed(Plane p) {
      p.log("landed");
      onRunway = waiting.poll();
      if (onRunway != null) onRunway.log("cleared to land");
    }
  }

  static final class Plane {
    final String name;
    final Tower tower;

    Plane(String name, Tower tower) {
      this.name = name;
      this.tower = tower;
    }

    void log(String s) {
      System.out.println(name + ": " + s);
    }
  }

  public static void main(String[] args) {
    Tower t = new Tower();
    Plane a = new Plane("A1", t);
    Plane b = new Plane("B2", t);
    Plane c = new Plane("C3", t);
    t.requestLanding(a);
    t.requestLanding(b);
    t.requestLanding(c);
    t.landed(a);
    t.landed(b);
  }
}
