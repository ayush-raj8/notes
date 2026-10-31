package lld.chess.exam;

import lld.chess.*;

public class PredictSealedStatus {
  public static void main(String[] args) {
    GameStatus a = new GameStatus.InProgress(Color.WHITE, true);
    GameStatus b = new GameStatus.Checkmate(Color.BLACK);
    GameStatus c = new GameStatus.Stalemate();
    for (GameStatus s : new GameStatus[] {a, b, c}) {
      System.out.println(StatusText.of(s));
    }
  }
}
