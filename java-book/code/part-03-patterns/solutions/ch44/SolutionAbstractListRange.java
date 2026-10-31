package solutions.ch44;

import java.util.AbstractList;

/** Exercise 4: a read-only list of multiples built by writing only get and size. */
public final class SolutionAbstractListRange {
  static final class Multiples extends AbstractList<Integer> {
    private final int step;
    private final int count;

    Multiples(int step, int count) {
      this.step = step;
      this.count = count;
    }

    @Override
    public Integer get(int index) {
      if (index < 0 || index >= count) throw new IndexOutOfBoundsException();
      return step * (index + 1);
    }

    @Override
    public int size() {
      return count;
    }
  }

  public static void main(String[] args) {
    Multiples m = new Multiples(7, 5);
    System.out.println(m);
    System.out.println(m.stream().mapToInt(Integer::intValue).sum());
    System.out.println(m.contains(21) + " " + m.contains(22));
  }
}
