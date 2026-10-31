package solutions.ch49;

import java.util.ArrayList;
import java.util.List;

/** Exercise 2: snapshot a list of mutable objects; copy the list and each element. */
public final class SolutionDeepSnapshot {
  static final class Cell {
    int v;

    Cell(int v) {
      this.v = v;
    }

    Cell copy() {
      return new Cell(v);
    }

    @Override
    public String toString() {
      return Integer.toString(v);
    }
  }

  static List<Cell> snapshot(List<Cell> live) {
    List<Cell> out = new ArrayList<>();
    for (Cell c : live) out.add(c.copy());
    return out;
  }

  public static void main(String[] args) {
    List<Cell> live = new ArrayList<>(List.of(new Cell(1), new Cell(2)));
    List<Cell> shallow = List.copyOf(live);
    List<Cell> deep = snapshot(live);
    live.get(0).v = 99;
    System.out.println("live " + live + " shallow " + shallow + " deep " + deep);
  }
}
