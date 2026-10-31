package lld.snakeladder.exam;

import lld.snakeladder.*;

public class PredictJumpRecord {
  public static void main(String[] args) {
    Jump s = new Snake(14, 4);
    System.out.println(s);
    System.out.println(s instanceof Ladder);
    System.out.println(s.equals(new Snake(14, 4)));
    System.out.println(s.equals(new Ladder(4, 14)));
    try {
      new Snake(3, 5);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
