import lld.chess.Color;
import lld.chess.Piece;
import lld.chess.Board;
import lld.chess.Square;
import java.util.List;

public class ChessPermitsBroken {
  record Amazon(Color color) implements Piece {
    public char letter() {
      return 'A';
    }

    public List<Square> pseudoLegalTargets(Board board, Square from) {
      return List.of();
    }
  }
}
