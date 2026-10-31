package lld.tictactoe.exam;

import lld.tictactoe.Position;

public class PredictRecord {
  public static void main(String[] args) {
    Position a = new Position(1, 2);
    Position b = new Position(1, 2);
    System.out.println(a == b);
    System.out.println(a.equals(b));
    System.out.println(a);
    System.out.println(a.hashCode() == b.hashCode());
    try {
      new Position(-1, 0);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
