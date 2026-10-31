import lld.chess.Color;
import lld.chess.Square;
import lld.chess.King;

public class ChessRecordMutateBroken {
  static void recolor(King k) {
    k.color = Color.BLACK;
  }

  static void shift(Square s) {
    s.file = 0;
  }
}
